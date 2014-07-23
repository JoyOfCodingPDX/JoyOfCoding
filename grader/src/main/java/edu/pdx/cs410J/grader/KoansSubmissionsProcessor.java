package edu.pdx.cs410J.grader;

import com.google.common.io.ByteStreams;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class KoansSubmissionsProcessor extends StudentEmailAttachmentProcessor{
  public KoansSubmissionsProcessor(File directory, GradeBook gradeBook) {
    super(directory, gradeBook);
  }

  @Override
  public void processAttachment(Message message, String fileName, InputStream inputStream) {
    String studentName;
    try {
      studentName = getSenderName(message);

    } catch (SubmissionException ex) {
      logException("While getting student name", ex);
      return;
    }

    try {
      File file = getKoansFileLocation(fileName, studentName);
      warn("Writing \"" + fileName + "\" to " + file);

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
  }

  private String getSubmissionNote(Message message) throws SubmissionException {
    return "Submitted by " + getSenderName(message) + " on " + getSentDate(message);
  }

  private String getSentDate(Message message) throws SubmissionException {
    DateFormat format = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
    try {
      return format.format(message.getSentDate());
    } catch (MessagingException e) {
      throw new SubmissionException("While getting the sent date", e);
    }
  }

  private Assignment getKoansProjectFromGradeBook() throws SubmissionException {
    Assignment assignment = gradeBook.getAssignment("koans");
    if (assignment == null) {
      throw new SubmissionException("Could not find \"koans\" assignment in grade book");
    }
    return assignment;
  }

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
    return gradeBook.getStudentIds().stream().map(gradeBook::getStudent);
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

  private File getKoansFileLocation(String fileName, String studentName) throws IOException {
    File dir = new File(directory, studentName);
    if (!dir.exists() && !dir.mkdirs()) {
      throw new IOException("Could not create directory \"" + dir + "\"");
    }
    return new File(dir, fileName);
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

  @Override
  public String getEmailFolder() {
    return "koans";
  }
}
