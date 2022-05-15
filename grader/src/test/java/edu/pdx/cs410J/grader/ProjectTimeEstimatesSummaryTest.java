package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.grader.ProjectTimeEstimatesSummary.TimeEstimatesSummaries;
import edu.pdx.cs410J.grader.ProjectTimeEstimatesSummary.TimeEstimatesSummary;
import edu.pdx.cs410J.grader.gradebook.Assignment;
import edu.pdx.cs410J.grader.gradebook.Assignment.ProjectType;
import edu.pdx.cs410J.grader.gradebook.Grade;
import edu.pdx.cs410J.grader.gradebook.GradeBook;
import edu.pdx.cs410J.grader.gradebook.Student;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static edu.pdx.cs410J.grader.ProjectTimeEstimatesSummary.TimeEstimatesSummary.*;
import static edu.pdx.cs410J.grader.gradebook.Assignment.ProjectType.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesRegex;

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
    return addProject(book, "project", projectType);
  }

  private Assignment addProject(GradeBook book, String projectName, ProjectType projectType) {
    Assignment project = new Assignment(projectName, 1.0)
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
    TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
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
    TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
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
    TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
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
    TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
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
    TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
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
    TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
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
    TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
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
  void upperQuartileOfThreeValuesIsAverageOfSecondAndThirdValue() {
    assertThat(upperQuartile(List.of(1.0, 2.0, 3.0)), equalTo(2.5));
  }

  @Test
  void upperQuartileOfFourValuesIsAverageOfThirdAndFourthValues() {
    assertThat(upperQuartile(List.of(1.0, 2.0, 3.0, 4.0)), equalTo(3.5));
  }

  @Test
  void averageFromMultipleStudents() {
    GradeBook book = new GradeBook("test");

    ProjectType projectType = APP_CLASSES;

    Assignment project = addProject(book, projectType);
    noteSubmission(addStudent(book, "student1"), project, 1.0);
    noteSubmission(addStudent(book, "student2"), project, 2.0);
    noteSubmission(addStudent(book, "student4"), project, 4.0);
    noteSubmission(addStudent(book, "student5"), project, 5.0);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
    assertThat(estimates.getAverage(), equalTo(3.0));
  }

  @Test
  void generateMarkdown() throws IOException {
    GradeBook book = new GradeBook("test");

    Assignment appClasses = addProject(book, "AppClasses", APP_CLASSES);
    Assignment textFile = addProject(book, "TextFile", TEXT_FILE);
    
    Student student1 = addStudent(book, "student1");
    Student student2 = addStudent(book, "student2");
    Student student3 = addStudent(book, "student3");
    Student student4 = addStudent(book, "student4");
    Student student5 = addStudent(book, "student5");
    
    noteSubmission(student1, appClasses, 4.0);    
    noteSubmission(student2, appClasses, 5.0);    
    noteSubmission(student3, appClasses, 3.0);    
    noteSubmission(student4, appClasses, 6.0);    
    noteSubmission(student5, appClasses, 2.0);    
    noteSubmission(student1, textFile, 7.0);
    noteSubmission(student2, textFile, 6.0);
    noteSubmission(student3, textFile, 8.0);
    noteSubmission(student4, textFile, 6.0);    
    noteSubmission(student5, textFile, 7.0);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    TimeEstimatesSummary appClassesEstimates = summaries.getTimeEstimateSummary(appClasses.getProjectType());
    assertThat(appClassesEstimates.getCount(), equalTo(5));

    StringWriter sw = new StringWriter();
    summaries.generateMarkdown(sw, List.of(appClasses.getProjectType(), textFile.getProjectType()));
    String markdown = sw.toString();

    System.out.println(markdown);

    List<String> lines = markdown.lines().collect(Collectors.toList());

    assertThat(lines.get(0), equalTo("|  | App Classes | Text File |"));
    assertThat(lines.get(1), equalTo("| :--- | ---: | ---: |"));
    assertThat(lines.get(2), equalTo("| Count | 5 | 5 |"));
    assertThat(lines.get(3), matchesRegex("\\| Average \\| \\d\\.\\d hrs \\| \\d\\.\\d hrs \\|"));
    assertThat(lines.get(4), matchesRegex("\\| Maximum \\| \\d\\.\\d hrs \\| \\d\\.\\d hrs \\|"));
  }
}
