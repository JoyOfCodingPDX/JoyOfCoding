package edu.pdx.cs410J.grader;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static edu.pdx.cs410J.grader.Submit.ManifestAttributes.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    Manifest manifest = manifest()
      .setProjectName(projectName)
      .setStudentName(studentName)
      .setStudentId(wrongStudentId)
      .setStudentEmail(wrongEmail)
      .setSubmissionComment(submissionComment)
      .build();

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

    Manifest manifest = manifest()
      .setProjectName(projectName)
      .setStudentName(studentName)
      .setStudentId(wrongStudentId)
      .setStudentEmail(wrongEmail)
      .setSubmissionComment(submissionComment)
      .build();

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

    Manifest manifest = manifest()
      .setProjectName(projectName)
      .setStudentName(studentName)
      .setStudentId(wrongStudentId)
      .setStudentEmail(email)
      .setSubmissionComment(submissionComment)
      .build();

    noteProjectSubmissionInGradeBook(gradebook, manifest);

    assertThat(gradebook.getStudent(wrongStudentId), isNotPresent());

    assertThatProjectSubmissionWasRecordedForStudent(projectName, student);
  }

  @Test
  public void submissionDoesNotMatchAnyStudentInGradeBook() {
    String projectName = "Project";

    GradeBook gradebook = createGradeBookWithAssignment(projectName);

    String studentName = "Not the student name we expect";
    String wrongStudentId = "Not the student id we expect";
    String wrongEmail = "Not the email we expect";
    String submissionComment = "This is only a test";

    Manifest manifest = manifest()
      .setProjectName(projectName)
      .setStudentName(studentName)
      .setStudentId(wrongStudentId)
      .setStudentEmail(wrongEmail)
      .setSubmissionComment(submissionComment)
      .build();

    assertThrows(StudentEmailAttachmentProcessor.SubmissionException.class, () ->
      noteProjectSubmissionInGradeBook(gradebook, manifest)
    );
  }

  @Test
  public void submissionTimeNotedInGradeBook() throws StudentEmailAttachmentProcessor.SubmissionException {
    String projectName = "Project";

    GradeBook gradebook = createGradeBookWithAssignment(projectName);
    Student student = createStudentInGradeBook(gradebook);

    String submissionComment = "This is only a test";
    LocalDateTime submissionDate = LocalDateTime.now().minusHours(2).withNano(0);
    Manifest manifest = manifest()
      .setProjectName(projectName)
      .setStudent(student)
      .setSubmissionDate(submissionDate)
      .setSubmissionComment(submissionComment)
      .build();

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
    Manifest manifest = manifest()
      .setProjectName(projectName)
      .setStudent(student)
      .setSubmissionDate(submissionDate)
      .setSubmissionComment(submissionComment)
      .build();

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
    Manifest manifest = manifest()
      .setProjectName(projectName)
      .setStudent(student)
      .setSubmissionDate(submissionDate)
      .setSubmissionComment(submissionComment)
      .build();

    noteProjectSubmissionInGradeBook(gradebook, manifest);

    assertThat(student.getLate(), not(contains(projectName)));
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

  // Test match on email and nickname

  private static Matcher<? super Optional<Student>> isNotPresent() {
    return new TypeSafeMatcher<>() {
      @Override
      protected boolean matchesSafely(Optional<Student> item) {
        return item.isEmpty();
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("an Optional<Student> that is not present");
      }
    };
  }

  private ManifestBuilder manifest() {
    return new ManifestBuilder()
      .setSubmissionDate(LocalDateTime.now())
      ;
  }

  private static class ManifestBuilder {
    Manifest manifest = new Manifest();

    private ManifestBuilder setAttribute(Attributes.Name name, String value) {
      this.manifest.getMainAttributes().put(name, value);
      return this;
    }

    Manifest build() {
      return manifest;
    }

    public ManifestBuilder setProjectName(String projectName) {
      return setAttribute(PROJECT_NAME, projectName);
    }

    public ManifestBuilder setStudent(Student student) {
      return setStudentId(student.getId())
        .setStudentName(student.getFullName())
        .setStudentEmail(student.getEmail())
        ;
    }

    private ManifestBuilder setStudentId(String studentId) {
      return setAttribute(USER_ID, studentId);
    }

    public ManifestBuilder setStudentName(String studentName) {
      return setAttribute(USER_NAME, studentName);
    }

    public ManifestBuilder setStudentEmail(String studentEmail) {
      return setAttribute(USER_EMAIL, studentEmail);
    }

    public ManifestBuilder setSubmissionDate(LocalDateTime submissionDate) {
      return setSubmissionDate(Submit.ManifestAttributes.formatSubmissionTime(submissionDate));
    }

    private ManifestBuilder setSubmissionDate(String submissionDate) {
      return setAttribute(SUBMISSION_TIME, submissionDate);
    }

    public ManifestBuilder setSubmissionComment(String submissionComment) {
      return setAttribute(SUBMISSION_COMMENT, submissionComment);
    }

    public ManifestBuilder setEstimatedHours(Double estimatedHours) {
      return setAttribute(ESTIMATED_HOURS, String.valueOf(estimatedHours));
    }
  }

}
