package edu.pdx.cs410J.grader;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import java.io.File;
import java.util.Date;
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
  public void matchStudentBasedOnNameBeforeId() throws StudentEmailAttachmentProcessor.SubmissionException {
    String projectName = "Project";

    GradeBook gradebook = createGradeBookWithAssignment(projectName);

    String studentId = "studentId";
    String firstName = "firstName";
    String lastName = "lastName";
    String nickName = "nickName";
    String email = "test@test.com";

    Student student = createStudentInGradeBook(studentId, firstName, lastName, nickName, email, gradebook);

    String studentName = firstName + " " + lastName;
    String wrongStudentId = "Not the student id we expect";
    String wrongEmail = "Not the email that we expect";
    String submissionComment = "This is only a test";

    Manifest manifest = createManifest(projectName, studentName, wrongStudentId, wrongEmail, submissionComment);

    noteProjectSubmissionInGradeBook(gradebook, manifest);

    assertThat(gradebook.getStudent(wrongStudentId), isNotPresent());

    assertThatProjectSubmissionWasRecordedForStudent(projectName, student);
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
    Manifest manifest = new Manifest();
    Attributes attributes = manifest.getMainAttributes();
    attributes.put(USER_ID, wrongStudentId);
    attributes.put(USER_EMAIL, wrongEmail);
    attributes.put(USER_NAME, studentName);
    attributes.put(PROJECT_NAME, projectName);
    attributes.put(SUBMISSION_TIME, Submit.ManifestAttributes.formatSubmissionTime(new Date()));
    attributes.put(SUBMISSION_COMMENT, submissionComment);
    return manifest;
  }

  private Student createStudentInGradeBook(String studentId, String firstName, String lastName, String nickName, String email, GradeBook gradebook) {
    Student student = new Student(studentId);
    student.setFirstName(firstName);
    student.setLastName(lastName);
    student.setNickName(nickName);
    student.setEmail(email);
    gradebook.addStudent(student);
    return student;
  }

  private GradeBook createGradeBookWithAssignment(String projectName) {
    GradeBook gradebook = new GradeBook("test");
    Assignment assignment = new Assignment(projectName, 10.0);
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
