package edu.pdx.cs410J.grader;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.ByteStreams;

import javax.mail.Message;
import java.io.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static edu.pdx.cs410J.grader.Submit.ManifestAttributes.*;

class ProjectSubmissionsProcessor extends StudentEmailAttachmentProcessor {

  @VisibleForTesting
  static final String EMAIL_FOLDER_NAME = "Project Submissions";

  public ProjectSubmissionsProcessor(File directory, GradeBook gradeBook) {
    super(directory, gradeBook);
  }

  @Override
  public Iterable<? extends String> getSupportedContentTypes() {
    return Collections.singleton("application/zip");
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

    LocalDateTime submissionTime = getSubmissionTime(attrs);
    grade.addSubmissionTime(submissionTime);

    if (project.isSubmissionLate(submissionTime)) {
      student.addLate(project.getName());
    }
  }

  private LocalDateTime getSubmissionTime(Attributes attrs) throws SubmissionException {
    String string = getSubmissionTimeString(attrs);
    return Submit.ManifestAttributes.parseSubmissionTime(string);
  }

  private String getSubmissionNote(Attributes attrs) throws SubmissionException {
    String studentName = getManifestAttributeValue(attrs, USER_NAME, "Student name missing from manifest");
    String submissionTime = getSubmissionTimeString(attrs);
    String submissionComment = getManifestAttributeValue(attrs, SUBMISSION_COMMENT, "Submission comment missing from manifest");

    return "Submitted by: " + studentName + "\n" +
      "On: " + submissionTime + "\n" +
      "With comment: " + submissionComment + "\n";
  }

  private String getSubmissionTimeString(Attributes attrs) throws SubmissionException {
    return getManifestAttributeValue(attrs, SUBMISSION_TIME, "Submission time missing from manifest");
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
    String studentEmail = getManifestAttributeValue(attrs, USER_EMAIL, "Student Email missing from manifest");

    Optional<Student> optional = this.gradeBook.studentsStream().filter(student ->
      hasStudentId(student, studentId) ||
      hasFirstNameLastName(student, studentName) ||
      hasNickNameLastName(student, studentName) ||
      hasEmail(student, studentEmail)).findAny();

    return optional.orElseThrow(() -> {
      String s = "Could not find student with id \"" + studentId + "\" or name \"" +
        studentName + "\" or email \"" + studentEmail + "\" in grade book";
      return new SubmissionException(s);
    });
  }

  private boolean hasEmail(Student student, String studentEmail) {
    return studentEmail.equals(student.getEmail());
  }

  private boolean hasNickNameLastName(Student student, String studentName) {
    return studentName.equals(student.getNickName() + " " + student.getLastName());
  }

  private boolean hasFirstNameLastName(Student student, String studentName) {
    return studentName.equals(student.getFirstName() + " " + student.getLastName());
  }

  private boolean hasStudentId(Student student, String studentId) {
    return student.getId().equals(studentId);
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
    ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(file));
    for (ZipEntry entry = in.getNextEntry(); entry != null ; entry = in.getNextEntry()) {
      if (entry.getName().equals(JarFile.MANIFEST_NAME)) {
        Manifest manifest = new Manifest();
        manifest.read(in);
        in.closeEntry();
        return manifest;

      } else {
        in.closeEntry();
      }
    }

    throw new IllegalStateException("Zip file did not contain manifest");
  }

  private void warnOfPreExistingFile(File file) {
    warn("Overwriting existing file \"" + file + "\"");
  }

  @Override
  public String getEmailFolder() {
    return EMAIL_FOLDER_NAME;
  }

}
