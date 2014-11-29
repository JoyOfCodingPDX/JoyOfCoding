package edu.pdx.cs410J.grader;

import org.junit.Test;

import java.io.File;
import java.util.Date;
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
    GradeBook gradebook = new GradeBook("test");

    String projectName = "Project";
    Assignment assignment = new Assignment(projectName, 10.0);
    gradebook.addAssignment(assignment);

    Student student = new Student("studentId");
    String firstName = "firstName";
    String lastName = "lastName";
    String studentName = firstName + " " + lastName;
    student.setFirstName(firstName);
    student.setLastName(lastName);
    student.setEmail("test@test.com");
    gradebook.addStudent(student);



    Manifest manifest = new Manifest();
    Attributes attributes = manifest.getMainAttributes();
    String wrongStudentId = "Not the student id we expect";
    attributes.put(USER_ID, wrongStudentId);
    attributes.put(USER_EMAIL, "Not the email that we expect");
    attributes.put(USER_NAME, studentName);
    attributes.put(PROJECT_NAME, projectName);
    attributes.put(SUBMISSION_TIME, Submit.ManifestAttributes.formatSubmissionTime(new Date()));
    String submissionComment = "This is only a test";
    attributes.put(SUBMISSION_COMMENT, submissionComment);

    ProjectSubmissionsProcessor processor =
      new ProjectSubmissionsProcessor(new File(System.getProperty("user.dir")), gradebook);
    processor.noteSubmissionInGradeBook(manifest);

    assertThat(gradebook.getStudent(wrongStudentId), is(nullValue()));

    Grade grade = student.getGrade(assignment);
    assertThat(grade, not(nullValue()));
    assertThat(grade.isNotGraded(), equalTo(true));
    assertThat(grade.getNotes(), contains(submissionComment));
  }
}
