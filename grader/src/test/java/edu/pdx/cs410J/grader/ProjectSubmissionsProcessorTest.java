package edu.pdx.cs410J.grader;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static edu.pdx.cs410J.grader.Submit.ManifestAttributes.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ProjectSubmissionsProcessorTest {

  /**
   * Given: A submission from a student whose name is in the grade book,
   * but whose id is not
   *
   * When: The submission is recorded
   *
   * Then: The submission is noted under the expected id and a new Student
   * is <b>not</b> created.
   *
   * This tests issue #52 (https://github.com/DavidWhitlock/PortlandStateJava/issues/52)
   */
  @Test
  public void matchStudentBasedOnFirstAndLastName() throws StudentEmailAttachmentProcessor.SubmissionException {
    String projectName = "Project";

    GradeBook gradebook = createGradeBookWithAssignment(projectName);
    Student student = createStudentInGradeBook(gradebook);

    String studentName = student.getFirstName() + " " + student.getLastName();
    String wrongStudentId = "Not the student id we expect";
    String wrongEmail = "Not the email that we expect";
    String submissionComment = "This is only a test";

    Manifest manifest = createManifest(projectName, studentName, wrongStudentId, wrongEmail, submissionComment);

    noteProjectSubmissionInGradeBook(gradebook, manifest);

    assertThat(gradebook.getStudent(wrongStudentId), isNotPresent());

    assertThatProjectSubmissionWasRecordedForStudent(projectName, student);
  }

  @Test
  public void matchStudentBasedOnNickAndLastName() throws StudentEmailAttachmentProcessor.SubmissionException {
    String projectName = "Project";

    GradeBook gradebook = createGradeBookWithAssignment(projectName);
    Student student = createStudentInGradeBook(gradebook);

    String studentName = student.getNickName() + " " + student.getLastName();
    String wrongStudentId = "Not the student id we expect";
    String wrongEmail = "Not the email that we expect";
    String submissionComment = "This is only a test";

    Manifest manifest = createManifest(projectName, studentName, wrongStudentId, wrongEmail, submissionComment);

    noteProjectSubmissionInGradeBook(gradebook, manifest);

    assertThat(gradebook.getStudent(wrongStudentId), isNotPresent());

    assertThatProjectSubmissionWasRecordedForStudent(projectName, student);
  }

  @Test
  public void matchStudentBasedOnEmail() throws StudentEmailAttachmentProcessor.SubmissionException {
    String projectName = "Project";

    GradeBook gradebook = createGradeBookWithAssignment(projectName);
    Student student = createStudentInGradeBook(gradebook);

    String studentName = "Not the student name we expect";
    String wrongStudentId = "Not the student id we expect";
    String email = student.getEmail();
    String submissionComment = "This is only a test";

    Manifest manifest = createManifest(projectName, studentName, wrongStudentId, email, submissionComment);

    noteProjectSubmissionInGradeBook(gradebook, manifest);

    assertThat(gradebook.getStudent(wrongStudentId), isNotPresent());

    assertThatProjectSubmissionWasRecordedForStudent(projectName, student);
  }

  @Test(expected = StudentEmailAttachmentProcessor.SubmissionException.class)
  public void submissionDoesNotMatchAnyStudentInGradeBook() throws StudentEmailAttachmentProcessor.SubmissionException {
    String projectName = "Project";

    GradeBook gradebook = createGradeBookWithAssignment(projectName);
    Student student = createStudentInGradeBook(gradebook);

    String studentName = "Not the student name we expect";
    String wrongStudentId = "Not the student id we expect";
    String wrongEmail = "Not the email we expect";
    String submissionComment = "This is only a test";

    Manifest manifest = createManifest(projectName, studentName, wrongStudentId, wrongEmail, submissionComment);

    noteProjectSubmissionInGradeBook(gradebook, manifest);

    assertThat(gradebook.getStudent(wrongStudentId), isNotPresent());

    assertThatProjectSubmissionWasRecordedForStudent(projectName, student);
  }

  @Test
  public void submissionTimeNotedInGradeBook() throws StudentEmailAttachmentProcessor.SubmissionException {
    String projectName = "Project";

    GradeBook gradebook = createGradeBookWithAssignment(projectName);
    Student student = createStudentInGradeBook(gradebook);

    String submissionComment = "This is only a test";
    LocalDateTime submissionDate = LocalDateTime.now().minusHours(2).withNano(0);
    Manifest manifest = createManifest(projectName, student, submissionDate, submissionComment);

    noteProjectSubmissionInGradeBook(gradebook, manifest);

    assertThat(student.getGrade(projectName).getSubmissionTimes(), contains(submissionDate));
  }

  @Test
  public void submissionsPastDueDateAreLate() throws StudentEmailAttachmentProcessor.SubmissionException {
    String projectName = "Project";
    LocalDateTime dueDate = LocalDateTime.now();

    GradeBook gradebook = createGradeBookWithAssignment(projectName, dueDate);
    Student student = createStudentInGradeBook(gradebook);

    String submissionComment = "This is only a test";
    LocalDateTime submissionDate = dueDate.plusDays(3);
    Manifest manifest = createManifest(projectName, student, submissionDate, submissionComment);

    noteProjectSubmissionInGradeBook(gradebook, manifest);

    assertThat(student.getLate(), contains(projectName));
  }

  @Test
  public void submissionsBeforeDueDateAreNoteLate() throws StudentEmailAttachmentProcessor.SubmissionException {
    String projectName = "Project";
    LocalDateTime dueDate = LocalDateTime.now();

    GradeBook gradebook = createGradeBookWithAssignment(projectName, dueDate);
    Student student = createStudentInGradeBook(gradebook);

    String submissionComment = "This is only a test";
    LocalDateTime submissionDate = dueDate.minusDays(3);
    Manifest manifest = createManifest(projectName, student, submissionDate, submissionComment);

    noteProjectSubmissionInGradeBook(gradebook, manifest);

    assertThat(student.getLate(), not(contains(projectName)));
  }

  private Manifest createManifest(String projectName, Student student, LocalDateTime submissionDate, String submissionComment) {
    return createManifest(projectName, student.getFullName(), student.getId(), student.getEmail(), submissionComment,
      Submit.ManifestAttributes.formatSubmissionTime(submissionDate));
  }

  private void noteProjectSubmissionInGradeBook(GradeBook gradebook, Manifest manifest) throws StudentEmailAttachmentProcessor.SubmissionException {
    ProjectSubmissionsProcessor processor =
      new ProjectSubmissionsProcessor(new File(System.getProperty("user.dir")), gradebook);
    processor.noteSubmissionInGradeBook(manifest);
  }

  private void assertThatProjectSubmissionWasRecordedForStudent(String projectName, Student student) {
    Grade grade = student.getGrade(projectName);
    assertThat(grade, not(nullValue()));
    assertThat(grade.isNotGraded(), equalTo(true));
  }

  private Manifest createManifest(String projectName, String studentName, String wrongStudentId, String wrongEmail, String submissionComment) {
    return createManifest(projectName, studentName, wrongStudentId, wrongEmail, submissionComment, Submit.ManifestAttributes.formatSubmissionTime(LocalDateTime.now()));
  }

  private Manifest createManifest(String projectName, String studentName, String wrongStudentId, String wrongEmail, String submissionComment, String submissionTime) {
    Manifest manifest = new Manifest();
    Attributes attributes = manifest.getMainAttributes();
    attributes.put(USER_ID, wrongStudentId);
    attributes.put(USER_EMAIL, wrongEmail);
    attributes.put(USER_NAME, studentName);
    attributes.put(PROJECT_NAME, projectName);
    attributes.put(SUBMISSION_TIME, submissionTime);
    attributes.put(SUBMISSION_COMMENT, submissionComment);
    return manifest;
  }

  private Student createStudentInGradeBook(GradeBook gradebook) {
    Student student = new Student("studentId");
    student.setFirstName("firstName");
    student.setLastName("lastName");
    student.setNickName("nickName");
    student.setEmail("test@test.com");
    gradebook.addStudent(student);
    return student;
  }

  private GradeBook createGradeBookWithAssignment(String projectName) {
    return createGradeBookWithAssignment(projectName, null);
  }

  private GradeBook createGradeBookWithAssignment(String projectName, LocalDateTime dueDate) {
    GradeBook gradebook = new GradeBook("test");
    Assignment assignment = new Assignment(projectName, 10.0);
    assignment.setDueDate(dueDate);
    gradebook.addAssignment(assignment);
    return gradebook;
  }

  // Test match on email and nick name

  private static Matcher<? super Optional<Student>> isNotPresent() {
    return new TypeSafeMatcher<Optional<Student>>() {
      @Override
      protected boolean matchesSafely(Optional<Student> item) {
        return !item.isPresent();
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("an Optional<Student> that is not present");
      }
    };
  }
}
