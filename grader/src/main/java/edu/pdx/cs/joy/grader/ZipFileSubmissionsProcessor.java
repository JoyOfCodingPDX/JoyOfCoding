package edu.pdx.cs.joy.grader;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.ByteStreams;
import edu.pdx.cs.joy.grader.gradebook.Assignment;
import edu.pdx.cs.joy.grader.gradebook.Grade;
import edu.pdx.cs.joy.grader.gradebook.GradeBook;
import edu.pdx.cs.joy.grader.gradebook.Student;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class ZipFileSubmissionsProcessor extends StudentEmailAttachmentProcessor {
  public ZipFileSubmissionsProcessor(File directory, GradeBook gradeBook) {
    super(directory, gradeBook);
  }

  @Override
  public Iterable<? extends String> getSupportedContentTypes() {
    return List.of("application/zip", "application/x-zip-compressed", "application/x-gzip");
  }

  @Override
  public void processAttachment(Message message, String fileName, InputStream inputStream, String contentType) {
    String studentId;
    try {
      studentId = getIdOfStudentInGradeBookWhoSent(message);

    } catch (SubmissionException ex) {
      logException("While getting student id", ex);
      return;
    }

    try {
      File file = getLocationToWriteFile(fileName, studentId);
      info("Writing \"" + fileName + "\" to " + file);

      ByteStreams.copy(inputStream, new FileOutputStream(file));
    } catch (IOException ex) {
      logException("While writing \"" + fileName + "\" to \"" + directory + "\"", ex);
    }

    try {
      noteSubmissionInGradeBook(message);

    } catch (SubmissionException ex) {
      logException("While noting submission from \"" + fileName + "\"", ex);
    }
  }

  private String getIdOfStudentInGradeBookWhoSent(Message message) throws SubmissionException {
    Student student = getStudentFromGradeBook(message);
    return student.getId();
  }

  private void noteSubmissionInGradeBook(Message message) throws SubmissionException {
    Student student = getStudentFromGradeBook(message);
    Assignment project = getKoansProjectFromGradeBook();
    String note = getSubmissionNote(message);

    Grade grade = student.getGrade(project);
    if (grade == null) {
      grade = new Grade(project, Grade.NO_GRADE);
      student.setGrade(project.getName(), grade);
    }
    grade.addNote(note);
    grade.noteSubmission(getSentDate(message));
  }

  private String getSubmissionNote(Message message) throws SubmissionException {
    String senderName = getSenderName(message);
    LocalDateTime sentDate = getSentDate(message);

    return getSubmissionNote(senderName, sentDate);
  }

  private LocalDateTime getSentDate(Message message) throws SubmissionException {
    Date sentDate;
    try {
      sentDate = message.getSentDate();
    } catch (MessagingException e) {
      throw new SubmissionException("While getting the sent date", e);
    }
    return LocalDateTime.ofInstant(sentDate.toInstant(), ZoneId.systemDefault());
  }

  @VisibleForTesting
  static String getSubmissionNote(String senderName, LocalDateTime submissionTime) {
    return "Submitted by " + senderName + " on " + Submit.ManifestAttributes.formatSubmissionTime(submissionTime);
  }

  private Assignment getKoansProjectFromGradeBook() throws SubmissionException {
    Assignment assignment = gradeBook.getAssignment(getAssignmentName());
    if (assignment == null) {
      throw new SubmissionException("Could not find \"koans\" assignment in grade book");
    }
    return assignment;
  }

  protected abstract String getAssignmentName();

  private Student getStudentFromGradeBook(Message message) throws SubmissionException {
    String senderName = getSenderName(message);
    Optional<Student> student = getStudentWithName(senderName);
    if (student.isPresent()) {
      return student.get();
    }

    String senderAddress = getSenderEmailAddress(message);
    student = getStudentWithEmailAddress(senderAddress);
    return student.orElseThrow(() -> {
      String m = "Couldn't find student named \"" + senderName + "\" with email address \"" +
        senderAddress + "\" in gradebook";
      return new SubmissionException(m);
    });
  }

  private Optional<Student> getStudentWithEmailAddress(String senderAddress) {
    return getStudentsInGradeBook().filter(doesStudentHaveEmailAddress(senderAddress)).findAny();
  }

  private Predicate<Student> doesStudentHaveEmailAddress(String address) {
    return student -> student.getEmail().equals(address);
  }

  private String getSenderEmailAddress(Message message) throws SubmissionException {
    Address from = getSender(message);

    if (from instanceof InternetAddress) {
      return getEmailAddress((InternetAddress) from);
    } else {
      return from.toString();
    }
  }

  private Optional<Student> getStudentWithName(String studentName) {
    return getStudentsInGradeBook().filter(doesStudentHaveName(studentName)).findAny();
  }

  private Stream<Student> getStudentsInGradeBook() {
    return gradeBook.studentsStream();
  }

  private Predicate<Student> doesStudentHaveName(String studentName) {
    return student -> getFullName(student).equals(studentName) || getNickName(student).equals(studentName);
  }

  private String getNickName(Student student) {
    return student.getNickName() + " " + student.getLastName();
  }

  private String getFullName(Student student) {
    return student.getFirstName() + " " + student.getLastName();
  }

  private File getLocationToWriteFile(String fileName, String studentId) throws IOException {
    File dir = new File(directory, getAssignmentName());
    if (!dir.exists() && !dir.mkdirs()) {
      throw new IOException("Could not create directory \"" + dir + "\"");
    }
    return new File(dir, appendFileSuffixToStudentId(fileName, studentId));
  }

  private String appendFileSuffixToStudentId(String fileName, String studentId) {
    return studentId + fileName.substring(fileName.lastIndexOf('.'));
  }

  private String getSenderName(Message message) throws SubmissionException {
    Address from = getSender(message);

    if (from instanceof InternetAddress) {
      return getStudentName((InternetAddress) from);
    } else {
      return from.toString();
    }
  }

  private Address getSender(Message message) throws SubmissionException {
    Address from;
    try {
      from = message.getFrom()[0];

    } catch (MessagingException ex) {
      throw new SubmissionException("Could not get from address from " + message, ex);
    }
    return from;
  }

  private String getStudentName(InternetAddress from) {
    if (from.getPersonal() != null) {
      return from.getPersonal();

    } else {
      return getEmailAddress(from);
    }
  }

  private String getEmailAddress(InternetAddress from) {
    return from.getAddress();
  }
}
