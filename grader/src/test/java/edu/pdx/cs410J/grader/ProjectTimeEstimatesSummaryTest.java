package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.grader.ProjectTimeEstimatesSummary.TimeEstimatesSummaries;
import edu.pdx.cs410J.grader.gradebook.Assignment;
import edu.pdx.cs410J.grader.gradebook.Assignment.ProjectType;
import edu.pdx.cs410J.grader.gradebook.Grade;
import edu.pdx.cs410J.grader.gradebook.GradeBook;
import edu.pdx.cs410J.grader.gradebook.Student;
import org.junit.jupiter.api.Test;

import static edu.pdx.cs410J.grader.gradebook.Assignment.ProjectType.APP_CLASSES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProjectTimeEstimatesSummaryTest {

  private void noteSubmission(Student student, Assignment project, double... estimates) {
    Grade grade = new Grade(project, Grade.NO_GRADE);
    for (double estimate : estimates) {
      Grade.SubmissionInfo submission = new Grade.SubmissionInfo();
      submission.setEstimatedHours(estimate);
      grade.noteSubmission(submission);
    }
    student.setGrade(project, grade);
  }

  private Student addStudent(GradeBook book) {
    Student student = new Student("student");
    book.addStudent(student);
    return student;
  }

  private Assignment addProject(GradeBook book, ProjectType projectType) {
    Assignment project = new Assignment("project", 1.0)
      .setProjectType(projectType);
    book.addAssignment(project);
    return project;
  }

  @Test
  void oneSubmittedProjectHasSummaryWithOneSubmission() {
    GradeBook book = new GradeBook("test");

    ProjectType projectType = APP_CLASSES;

    noteSubmission(addStudent(book), addProject(book, projectType), 10.0);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    ProjectTimeEstimatesSummary.TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
    assertThat(estimates.getCount(), equalTo(1));
  }

  @Test
  void maximumEstimateIsConsidered() {
    GradeBook book = new GradeBook("test");

    ProjectType projectType = APP_CLASSES;

    double maximumEstimate = 11.0;
    noteSubmission(addStudent(book), addProject(book, projectType), 10.0, maximumEstimate);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    ProjectTimeEstimatesSummary.TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
    assertThat(estimates.getCount(), equalTo(1));
    assertThat(estimates.getMaximum(), equalTo(maximumEstimate));

  }

}
