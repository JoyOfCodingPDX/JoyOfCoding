package edu.pdx.cs410J.grader;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SummaryReportTest {

  @Test
  public void ungradedAssignmentsDoNotCountTowardsGraded() {
    GradeBook gradeBook = new GradeBook("test");
    Student student = new Student("student");
    gradeBook.addStudent(student);
    Assignment assignment = new Assignment("assignment", 4.0);
    gradeBook.addAssignment(assignment);

    Grade grade = new Grade(assignment, Grade.NO_GRADE);
    student.setGrade(assignment.getName(), grade);

    assertThat(SummaryReport.noStudentHasGradeFor(assignment, gradeBook), equalTo(true));

  }
}
