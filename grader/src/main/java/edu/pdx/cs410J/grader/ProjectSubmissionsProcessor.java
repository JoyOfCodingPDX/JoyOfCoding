package edu.pdx.cs410J.grader;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.ByteStreams;

import javax.mail.Message;
import java.io.*;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import static edu.pdx.cs410J.grader.Submit.ManifestAttributes.*;

class ProjectSubmissionsProcessor extends StudentEmailAttachmentProcessor {

  public ProjectSubmissionsProcessor(File directory, GradeBook gradeBook) {
    super(directory, gradeBook);
  }

  @Override
  public void processAttachment(Message message, String fileName, InputStream inputStream) {
    debug("    File name: " + fileName);
    debug("    InputStream: " + inputStream);

    byte[] bytes;
    try {
      bytes = readAttachmentIntoByteArray(inputStream);

    } catch (IOException ex) {
      logException("While copying \"" + fileName + " \" to a byte buffer", ex);
      return;
    }


    Manifest manifest;
    try {
      manifest = getManifestFromByteArray(bytes);

    } catch (IOException ex) {
      logException("While reading jar file \"" + fileName + "\"", ex);
      return;
    }

    try {
      writeSubmissionToDisk(fileName, bytes, manifest);

    } catch (IOException | SubmissionException ex) {
      logException("While writing \"" + fileName + "\" to \"" + directory + "\"", ex);
      return;
    }

    try {
      noteSubmissionInGradeBook(manifest);

    } catch (SubmissionException ex) {
      logException("While noting submission from \"" + fileName + "\"", ex);
    }

  }

  private byte[] readAttachmentIntoByteArray(InputStream inputStream) throws IOException {
    byte[] bytes;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ByteStreams.copy(inputStream, baos);
    bytes = baos.toByteArray();
    return bytes;
  }

  private void writeSubmissionToDisk(String fileName, byte[] bytes, Manifest manifest) throws SubmissionException, IOException {
    File projectDir = getProjectDirectory(manifest);

    File file = new File(projectDir, fileName);
    if (file.exists()) {
      warnOfPreExistingFile(file);
    }

    info("Writing " + fileName + " to " + projectDir);

    ByteStreams.copy(new ByteArrayInputStream(bytes), new FileOutputStream(file));
  }

  private File getProjectDirectory(Manifest manifest) throws SubmissionException, IOException {
    String projectName = getProjectNameFromManifest(manifest.getMainAttributes());
    File projectDir = new File(directory, projectName);
    if (!projectDir.exists()) {
      if(!projectDir.mkdirs()) {
        throw new IOException("Could not create directory: " + projectDir);
      }
    }
    return projectDir;
  }

  @VisibleForTesting
  void noteSubmissionInGradeBook(Manifest manifest) throws SubmissionException {
    Attributes attrs = manifest.getMainAttributes();

    Student student = getStudentFromGradeBook(attrs);
    Assignment project = getProjectFromGradeBook(attrs);
    String note = getSubmissionNote(attrs);

    Grade grade = student.getGrade(project);
    if (grade == null) {
      grade = new Grade(project, Grade.NO_GRADE);
      student.setGrade(project.getName(), grade);
    }
    grade.addNote(note);
  }

  private String getSubmissionNote(Attributes attrs) throws SubmissionException {
    String studentName = getManifestAttributeValue(attrs, USER_NAME, "Student name missing from manifest");
    String submissionTime = getManifestAttributeValue(attrs, SUBMISSION_TIME, "Submission time missing from manifest");
    String submissionComment = getManifestAttributeValue(attrs, SUBMISSION_COMMENT, "Submission comment missing from manifest");

    return "Submitted by: " + studentName + "\n" +
      "On: " + submissionTime + "\n" +
      "With comment: " + submissionComment + "\n";
  }

  private Assignment getProjectFromGradeBook(Attributes attrs) throws SubmissionException {
    String projectName = getProjectNameFromManifest(attrs);

    Assignment assignment = this.gradeBook.getAssignment(projectName);
    if (assignment == null) {
      throw new SubmissionException("Assignment with name \"" + projectName + "\" is not in grade book");
    }
    return assignment;
  }

  private String getProjectNameFromManifest(Attributes attrs) throws SubmissionException {
    return getManifestAttributeValue(attrs, PROJECT_NAME, "Project name missing from manifest");
  }

  private Student getStudentFromGradeBook(Attributes attrs) throws SubmissionException {
    String studentId = getManifestAttributeValue(attrs, USER_ID, "Student Id missing from manifest");
    String studentName = getManifestAttributeValue(attrs, USER_NAME, "Student Name missing from manifest");

    Optional<Student> student = findStudentInGradeBookWithId(studentId);
    if (student.isPresent()) {
      return student.get();

    } else {
      student = findStudentInGradeBookWithName(studentName);
      if (student.isPresent()) {
        return student.get();

      } else {
        String s = "Could not find student with id \"" + studentId + "\" or name \"" + studentName + "\" in grade book";
        throw new SubmissionException(s);
      }
    }
  }

  private Optional<Student> findStudentInGradeBookWithName(String studentName) {
    Optional<Student> student = findStudentInGradeBookWithFirstAndLastName(studentName);
    if (student.isPresent()) {
      return student;

    } else {
      return findStudentInGradeBookWithNickAndLastName(studentName);
    }
  }

  private Optional<Student> findStudentInGradeBookWithFirstAndLastName(String studentName) {
    Predicate<Student> hasName =
      student -> studentName.equals(student.getFirstName() + " " + student.getLastName());
    return this.gradeBook.studentsStream().filter(hasName).findAny();
  }

  private Optional<Student> findStudentInGradeBookWithNickAndLastName(String studentName) {
    Predicate<Student> hasName =
      student -> studentName.equals(student.getNickName() + " " + student.getLastName());
    return this.gradeBook.studentsStream().filter(hasName).findAny();
  }

  private Optional<Student> findStudentInGradeBookWithId(String studentId) {
    return this.gradeBook.getStudent(studentId);
  }

  private String getManifestAttributeValue(Attributes attrs, Attributes.Name attribute, String message) throws SubmissionException {
    String value = attrs.getValue(attribute);
    if (value == null) {
      throwSubmissionException(message);
    }
    return value;
  }

  private void throwSubmissionException(String message) throws SubmissionException {
    throw new SubmissionException(message);

  }

  private Manifest getManifestFromByteArray(byte[] file) throws IOException {
    JarInputStream in = new JarInputStream(new ByteArrayInputStream(file));
    return in.getManifest();
  }

  private void warnOfPreExistingFile(File file) {
    warn("Overwriting existing file \"" + file + "\"");
  }

  @Override
  public String getEmailFolder() {
    return "Project Submissions";
  }

}
