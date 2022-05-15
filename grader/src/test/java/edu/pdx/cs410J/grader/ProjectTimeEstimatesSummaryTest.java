package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.grader.ProjectTimeEstimatesSummary.TimeEstimatesSummaries;
import edu.pdx.cs410J.grader.gradebook.Assignment;
import edu.pdx.cs410J.grader.gradebook.Grade;
import edu.pdx.cs410J.grader.gradebook.GradeBook;
import edu.pdx.cs410J.grader.gradebook.Student;
import org.junit.jupiter.api.Test;

import static edu.pdx.cs410J.grader.gradebook.Assignment.ProjectType.APP_CLASSES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProjectTimeEstimatesSummaryTest {

  @Test
  void oneSubmittedProjectHasSummaryWithOneSubmission() {
    GradeBook book = new GradeBook("test");
    Student student = new Student("student");
    book.addStudent(student);

    Assignment project = new Assignment("project", 1.0)
      .setProjectType(APP_CLASSES);
    book.addAssignment(project);

    Grade grade = new Grade(project, Grade.NO_GRADE);
    Grade.SubmissionInfo submission = new Grade.SubmissionInfo();
    submission.setEstimatedHours(10.0);
    grade.noteSubmission(submission);
    student.setGrade(project, grade);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    ProjectTimeEstimatesSummary.TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(APP_CLASSES);
    assertThat(estimates.getCount(), equalTo(1));
  }
}
