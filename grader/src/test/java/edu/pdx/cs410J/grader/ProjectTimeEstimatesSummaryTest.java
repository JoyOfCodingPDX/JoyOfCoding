package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.grader.ProjectTimeEstimatesSummary.TimeEstimatesSummaries;
import edu.pdx.cs410J.grader.gradebook.Assignment;
import edu.pdx.cs410J.grader.gradebook.Assignment.ProjectType;
import edu.pdx.cs410J.grader.gradebook.Grade;
import edu.pdx.cs410J.grader.gradebook.GradeBook;
import edu.pdx.cs410J.grader.gradebook.Student;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static edu.pdx.cs410J.grader.ProjectTimeEstimatesSummary.TimeEstimatesSummary.*;
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

  private Student addStudent(GradeBook book, String studentName) {
    Student student = new Student(studentName);
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

    noteSubmission(addStudent(book, "student"), addProject(book, projectType), 10.0);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    ProjectTimeEstimatesSummary.TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
    assertThat(estimates.getCount(), equalTo(1));
  }

  @Test
  void maximumEstimateByStudentIsConsidered() {
    GradeBook book = new GradeBook("test");

    ProjectType projectType = APP_CLASSES;

    double maximumEstimate = 11.0;
    noteSubmission(addStudent(book, "student"), addProject(book, projectType), 10.0, maximumEstimate);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    ProjectTimeEstimatesSummary.TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
    assertThat(estimates.getCount(), equalTo(1));
    assertThat(estimates.getMaximum(), equalTo(maximumEstimate));

  }

  @Test
  void maximumFromMultipleStudents() {
    GradeBook book = new GradeBook("test");

    ProjectType projectType = APP_CLASSES;

    double maximumEstimate = 11.0;
    Assignment project = addProject(book, projectType);
    noteSubmission(addStudent(book, "student1"), project, 10.0, maximumEstimate);
    noteSubmission(addStudent(book, "student2"), project, 9);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    ProjectTimeEstimatesSummary.TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
    assertThat(estimates.getCount(), equalTo(2));
    assertThat(estimates.getMaximum(), equalTo(maximumEstimate));
  }

  @Test
  void minimumFromMultipleStudents() {
    GradeBook book = new GradeBook("test");

    ProjectType projectType = APP_CLASSES;

    double minimumEstimate = 9.0;
    Assignment project = addProject(book, projectType);
    noteSubmission(addStudent(book, "student1"), project, 10.0, 11.0);
    noteSubmission(addStudent(book, "student2"), project, minimumEstimate);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    ProjectTimeEstimatesSummary.TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
    assertThat(estimates.getCount(), equalTo(2));
    assertThat(estimates.getMinimum(), equalTo(minimumEstimate));
  }

  @Test
  void medianFromMultipleStudents() {
    GradeBook book = new GradeBook("test");

    ProjectType projectType = APP_CLASSES;

    Assignment project = addProject(book, projectType);
    double medianEstimate = 10.0;
    noteSubmission(addStudent(book, "student1"), project, medianEstimate);
    noteSubmission(addStudent(book, "student2"), project, 9.0);
    noteSubmission(addStudent(book, "student3"), project, 11.0);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    ProjectTimeEstimatesSummary.TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
    assertThat(estimates.getCount(), equalTo(3));
    assertThat(estimates.getMedian(), equalTo(medianEstimate));
  }

  @Test
  void medianOfOddNumberOfValuesIsMiddleValue() {
    double median = 2.0;
    Collection<Double> doubles = List.of(1.0, median, 3.0);
    assertThat(median(doubles), equalTo(median));
  }

  @Test
  void medianOfEvenNumberOfValuesIsAverageOfMiddleValues() {
    Collection<Double> doubles = List.of(1.0, 2.0, 3.0, 4.0);
    assertThat(median(doubles), equalTo(2.5));
  }

  @Test
  void medianSortsValues() {
    double median = 2.0;
    Collection<Double> doubles = List.of(median, 1.0, 3.0);
    assertThat(median(doubles), equalTo(median));
  }

  @Test
  void upperQuartileFromMultipleStudents() {
    GradeBook book = new GradeBook("test");

    ProjectType projectType = APP_CLASSES;

    double upperQuartile = 4.0;
    Assignment project = addProject(book, projectType);
    noteSubmission(addStudent(book, "student1"), project, 1.0);
    noteSubmission(addStudent(book, "student2"), project, 2.0);
    noteSubmission(addStudent(book, "student3"), project, 3.0);
    noteSubmission(addStudent(book, "student4"), project, upperQuartile);
    noteSubmission(addStudent(book, "student5"), project, 5.0);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    ProjectTimeEstimatesSummary.TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
    assertThat(estimates.getUpperQuartile(), equalTo(upperQuartile));
  }

  @Test
  void lowerQuartileFromMultipleStudents() {
    GradeBook book = new GradeBook("test");

    ProjectType projectType = APP_CLASSES;

    double lowerQuartile = 2.0;
    Assignment project = addProject(book, projectType);
    noteSubmission(addStudent(book, "student1"), project, 1.0);
    noteSubmission(addStudent(book, "student2"), project, lowerQuartile);
    noteSubmission(addStudent(book, "student3"), project, 3.0);
    noteSubmission(addStudent(book, "student4"), project, 4.0);
    noteSubmission(addStudent(book, "student5"), project, 5.0);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    ProjectTimeEstimatesSummary.TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
    assertThat(estimates.getLowerQuartile(), equalTo(lowerQuartile));
  }

  @Test
  void upperQuartileOfEvenNumberOfValues() {
    double upperQuartile = 5.0;
    List<Double> doubles = List.of(1.0, 2.0, 3.0, 4.0, upperQuartile, 6.0);
    assertThat(upperQuartile(doubles), equalTo(upperQuartile));
  }

  @Test
  void lowerQuartileOfEvenNumberOfValues() {
    double lowerQuartile = 2.0;
    List<Double> doubles = List.of(1.0, lowerQuartile, 3.0, 4.0, 5.0, 6.0);
    assertThat(lowerQuartile(doubles), equalTo(lowerQuartile));
  }

  @Test
  void lowerQuartileOfOddNumberOfValues() {
    double lowerQuartile = 2.0;
    List<Double> doubles = List.of(1.0, lowerQuartile, 3.0, 4.0, 5.0);
    assertThat(lowerQuartile(doubles), equalTo(lowerQuartile));
  }

  @Test
  void upperQuartileOfOneValueIsThatValue() {
    double value = 1.0;
    assertThat(upperQuartile(List.of(value)), equalTo(value));
  }

  @Test
  void upperQuartileOfTwoValuesIsSecondValue() {
    double value = 2.0;
    assertThat(upperQuartile(List.of(1.0, value)), equalTo(value));
  }

  @Test
  void upperQuartileOfThreeValuesIsThirdValue() {
    double value = 3.0;
    assertThat(upperQuartile(List.of(1.0, 2.0, value)), equalTo(value));
  }
}
