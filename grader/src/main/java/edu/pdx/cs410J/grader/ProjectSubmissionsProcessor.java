package edu.pdx.cs410J.grader;

import com.google.common.io.ByteStreams;

import java.io.*;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import static edu.pdx.cs410J.grader.Submit.ManifestAttributes.*;

class ProjectSubmissionsProcessor extends StudentEmailAttachmentProcessor {

  public ProjectSubmissionsProcessor(File directory, GradeBook gradeBook) {
    super(directory, gradeBook);
  }

  @Override
  public void processAttachment(String fileName, InputStream inputStream) {
    warn("    File name: " + fileName);
    warn("    InputStream: " + inputStream);

    File file = new File(directory, fileName);
    try {

      if (file.exists()) {
        warnOfPreExistingFile(file);
      }

      ByteStreams.copy(inputStream, new FileOutputStream(file));
    } catch (IOException ex) {
      logException("While writing \"" + fileName + "\" to \"" + directory + "\"", ex);
    }

    Manifest manifest;
    try {
      manifest = getManifestFromJarFile(file);

    } catch (IOException ex) {
      logException("While reading jar file \"" + fileName + "\"", ex);
      return;
    }

    try {
      noteSubmissionInGradeBook(manifest);

    } catch (SubmissionException ex) {
      logException("While noting submission from \"" + fileName + "\"", ex);
    }

  }

  private void noteSubmissionInGradeBook(Manifest manifest) throws SubmissionException {
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
    String projectName = getManifestAttributeValue(attrs, PROJECT_NAME, "Project name missing from manifest");

    Assignment assignment = this.gradeBook.getAssignment(projectName);
    if (assignment == null) {
      throw new SubmissionException("Assignment with name \"" + projectName + "\" is not in grade book");
    }
    return assignment;
  }

  private Student getStudentFromGradeBook(Attributes attrs) throws SubmissionException {
    String studentId = getManifestAttributeValue(attrs, USER_ID, "Student Id missing from manifest");

    Student student = this.gradeBook.getStudent(studentId);
    if (student == null) {
      throw new SubmissionException("Student with id \"" + studentId + "\" is not in grade book");
    }
    return student;
  }

  private String getManifestAttributeValue(Attributes attrs, Attributes.Name attribute, String message) throws SubmissionException {
    String studentId = attrs.getValue(attribute);
    if (studentId == null) {
      throwSubmissionException(message);
    }
    return studentId;
  }

  private void throwSubmissionException(String message) throws SubmissionException {
    throw new SubmissionException(message);

  }

  private Manifest getManifestFromJarFile(File file) throws IOException {
    JarInputStream in = new JarInputStream(new FileInputStream(file));
    return in.getManifest();
  }

  private void warnOfPreExistingFile(File file) {
    warn("Overwriting existing file \"" + file + "\"");
  }

  @Override
  public String getEmailFolder() {
    return "Project Submissions";
  }

  private class SubmissionException extends Exception {
    public SubmissionException(String message) {
      super(message);
    }
  }
}
