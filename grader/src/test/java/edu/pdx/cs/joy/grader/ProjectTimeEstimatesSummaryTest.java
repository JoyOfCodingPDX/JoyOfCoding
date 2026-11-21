package edu.pdx.cs.joy.grader;

import edu.pdx.cs.joy.grader.gradebook.Assignment;
import edu.pdx.cs.joy.grader.gradebook.Grade;
import edu.pdx.cs.joy.grader.gradebook.GradeBook;
import edu.pdx.cs.joy.grader.gradebook.Student;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ProjectTimeEstimatesSummaryTest {

  private void noteSubmission(Student student, Assignment project, Double... estimates) {
    Grade grade = new Grade(project, Grade.NO_GRADE);
    for (Double estimate : estimates) {
      Grade.SubmissionInfo submission = new Grade.SubmissionInfo();
      if (estimate != null) {
        submission.setEstimatedHours(estimate);
      }
      grade.noteSubmission(submission);
    }
    student.setGrade(project, grade);
  }

  private Student addStudent(GradeBook book, String studentName) {
    Student student = new Student(studentName);
    book.addStudent(student);
    return student;
  }

  private Assignment addProject(GradeBook book, Assignment.ProjectType projectType) {
    return addProject(book, "project", projectType);
  }

  private Assignment addProject(GradeBook book, String projectName, Assignment.ProjectType projectType) {
    Assignment project = new Assignment(projectName, 1.0)
      .setProjectType(projectType);
    book.addAssignment(project);
    return project;
  }

  @Test
  void oneSubmittedProjectHasSummaryWithOneSubmission() {
    GradeBook book = new GradeBook("test");

    Assignment.ProjectType projectType = Assignment.ProjectType.APP_CLASSES;

    noteSubmission(addStudent(book, "student"), addProject(book, projectType), 10.0);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    ProjectTimeEstimatesSummary.TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    ProjectTimeEstimatesSummary.TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
    assertThat(estimates.getCount(), equalTo(1));
  }

  @Test
  void maximumEstimateByStudentIsConsidered() {
    GradeBook book = new GradeBook("test");

    Assignment.ProjectType projectType = Assignment.ProjectType.APP_CLASSES;

    double maximumEstimate = 11.0;
    noteSubmission(addStudent(book, "student"), addProject(book, projectType), 10.0, maximumEstimate);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    ProjectTimeEstimatesSummary.TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    ProjectTimeEstimatesSummary.TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
    assertThat(estimates.getCount(), equalTo(1));
    assertThat(estimates.getMaximum(), equalTo(maximumEstimate));

  }

  @Test
  void maximumFromMultipleStudents() {
    GradeBook book = new GradeBook("test");

    Assignment.ProjectType projectType = Assignment.ProjectType.APP_CLASSES;

    double maximumEstimate = 11.0;
    Assignment project = addProject(book, projectType);
    noteSubmission(addStudent(book, "student1"), project, 10.0, maximumEstimate);
    noteSubmission(addStudent(book, "student2"), project, 9.0);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    ProjectTimeEstimatesSummary.TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    ProjectTimeEstimatesSummary.TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
    assertThat(estimates.getCount(), equalTo(2));
    assertThat(estimates.getMaximum(), equalTo(maximumEstimate));
  }

  @Test
  void minimumFromMultipleStudents() {
    GradeBook book = new GradeBook("test");

    Assignment.ProjectType projectType = Assignment.ProjectType.APP_CLASSES;

    double minimumEstimate = 9.0;
    Assignment project = addProject(book, projectType);
    noteSubmission(addStudent(book, "student1"), project, 10.0, 11.0);
    noteSubmission(addStudent(book, "student2"), project, minimumEstimate);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    ProjectTimeEstimatesSummary.TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    ProjectTimeEstimatesSummary.TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
    assertThat(estimates.getCount(), equalTo(2));
    assertThat(estimates.getMinimum(), equalTo(minimumEstimate));
  }

  @Test
  void medianFromMultipleStudents() {
    GradeBook book = new GradeBook("test");

    Assignment.ProjectType projectType = Assignment.ProjectType.APP_CLASSES;

    Assignment project = addProject(book, projectType);
    double medianEstimate = 10.0;
    noteSubmission(addStudent(book, "student1"), project, medianEstimate);
    noteSubmission(addStudent(book, "student2"), project, 9.0);
    noteSubmission(addStudent(book, "student3"), project, 11.0);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    ProjectTimeEstimatesSummary.TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    ProjectTimeEstimatesSummary.TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
    assertThat(estimates.getCount(), equalTo(3));
    assertThat(estimates.getMedian(), equalTo(medianEstimate));
  }

  @Test
  void medianOfOddNumberOfValuesIsMiddleValue() {
    double median = 2.0;
    Collection<Double> doubles = List.of(1.0, median, 3.0);
    MatcherAssert.assertThat(ProjectTimeEstimatesSummary.TimeEstimatesSummary.median(doubles), equalTo(median));
  }

  @Test
  void medianOfEvenNumberOfValuesIsAverageOfMiddleValues() {
    Collection<Double> doubles = List.of(1.0, 2.0, 3.0, 4.0);
    MatcherAssert.assertThat(ProjectTimeEstimatesSummary.TimeEstimatesSummary.median(doubles), equalTo(2.5));
  }

  @Test
  void medianSortsValues() {
    double median = 2.0;
    Collection<Double> doubles = List.of(median, 1.0, 3.0);
    MatcherAssert.assertThat(ProjectTimeEstimatesSummary.TimeEstimatesSummary.median(doubles), equalTo(median));
  }

  @Test
  void upperQuartileFromMultipleStudents() {
    GradeBook book = new GradeBook("test");

    Assignment.ProjectType projectType = Assignment.ProjectType.APP_CLASSES;

    double upperQuartile = 4.0;
    Assignment project = addProject(book, projectType);
    noteSubmission(addStudent(book, "student1"), project, 1.0);
    noteSubmission(addStudent(book, "student2"), project, 2.0);
    noteSubmission(addStudent(book, "student3"), project, 3.0);
    noteSubmission(addStudent(book, "student4"), project, upperQuartile);
    noteSubmission(addStudent(book, "student5"), project, 5.0);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    ProjectTimeEstimatesSummary.TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    ProjectTimeEstimatesSummary.TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
    assertThat(estimates.getUpperQuartile(), equalTo(upperQuartile));
  }

  @Test
  void lowerQuartileFromMultipleStudents() {
    GradeBook book = new GradeBook("test");

    Assignment.ProjectType projectType = Assignment.ProjectType.APP_CLASSES;

    double lowerQuartile = 2.0;
    Assignment project = addProject(book, projectType);
    noteSubmission(addStudent(book, "student1"), project, 1.0);
    noteSubmission(addStudent(book, "student2"), project, lowerQuartile);
    noteSubmission(addStudent(book, "student3"), project, 3.0);
    noteSubmission(addStudent(book, "student4"), project, 4.0);
    noteSubmission(addStudent(book, "student5"), project, 5.0);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    ProjectTimeEstimatesSummary.TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    ProjectTimeEstimatesSummary.TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
    assertThat(estimates.getLowerQuartile(), equalTo(lowerQuartile));
  }

  @Test
  void upperQuartileOfEvenNumberOfValues() {
    double upperQuartile = 5.0;
    List<Double> doubles = List.of(1.0, 2.0, 3.0, 4.0, upperQuartile, 6.0);
    MatcherAssert.assertThat(ProjectTimeEstimatesSummary.TimeEstimatesSummary.upperQuartile(doubles), equalTo(upperQuartile));
  }

  @Test
  void lowerQuartileOfEvenNumberOfValues() {
    double lowerQuartile = 2.0;
    List<Double> doubles = List.of(1.0, lowerQuartile, 3.0, 4.0, 5.0, 6.0);
    MatcherAssert.assertThat(ProjectTimeEstimatesSummary.TimeEstimatesSummary.lowerQuartile(doubles), equalTo(lowerQuartile));
  }

  @Test
  void lowerQuartileOfOddNumberOfValues() {
    double lowerQuartile = 2.0;
    List<Double> doubles = List.of(1.0, lowerQuartile, 3.0, 4.0, 5.0);
    MatcherAssert.assertThat(ProjectTimeEstimatesSummary.TimeEstimatesSummary.lowerQuartile(doubles), equalTo(lowerQuartile));
  }

  @Test
  void upperQuartileOfOneValueIsThatValue() {
    double value = 1.0;
    MatcherAssert.assertThat(ProjectTimeEstimatesSummary.TimeEstimatesSummary.upperQuartile(List.of(value)), equalTo(value));
  }

  @Test
  void upperQuartileOfTwoValuesIsSecondValue() {
    double value = 2.0;
    MatcherAssert.assertThat(ProjectTimeEstimatesSummary.TimeEstimatesSummary.upperQuartile(List.of(1.0, value)), equalTo(value));
  }

  @Test
  void upperQuartileOfThreeValuesIsAverageOfSecondAndThirdValue() {
    MatcherAssert.assertThat(ProjectTimeEstimatesSummary.TimeEstimatesSummary.upperQuartile(List.of(1.0, 2.0, 3.0)), equalTo(2.5));
  }

  @Test
  void upperQuartileOfFourValuesIsAverageOfThirdAndFourthValues() {
    MatcherAssert.assertThat(ProjectTimeEstimatesSummary.TimeEstimatesSummary.upperQuartile(List.of(1.0, 2.0, 3.0, 4.0)), equalTo(3.5));
  }

  @Test
  void averageFromMultipleStudents() {
    GradeBook book = new GradeBook("test");

    Assignment.ProjectType projectType = Assignment.ProjectType.APP_CLASSES;

    Assignment project = addProject(book, projectType);
    noteSubmission(addStudent(book, "student1"), project, 1.0);
    noteSubmission(addStudent(book, "student2"), project, 2.0);
    noteSubmission(addStudent(book, "student4"), project, 4.0);
    noteSubmission(addStudent(book, "student5"), project, 5.0);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    ProjectTimeEstimatesSummary.TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    ProjectTimeEstimatesSummary.TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
    assertThat(estimates.getAverage(), equalTo(3.0));
  }

  @Test
  void fivePercentThresholdsAreInterpolatedBetweenSortedEstimates() {
    List<Double> estimates = IntStream.rangeClosed(1, 100)
      .mapToDouble(i -> 101 - i)
      .boxed()
      .collect(Collectors.toList());
    ProjectTimeEstimatesSummary.TimeEstimatesSummary summary =
      new ProjectTimeEstimatesSummary.TimeEstimatesSummary(estimates);

    assertThat(summary.getBottomFivePercent(), closeTo(5.95, 0.000001));
    assertThat(summary.getTopFivePercent(), closeTo(95.05, 0.000001));
  }

  @Test
  void fivePercentThresholdsForOneEstimateAreThatEstimate() {
    ProjectTimeEstimatesSummary.TimeEstimatesSummary summary =
      new ProjectTimeEstimatesSummary.TimeEstimatesSummary(List.of(10.0));

    assertThat(summary.getBottomFivePercent(), equalTo(10.0));
    assertThat(summary.getTopFivePercent(), equalTo(10.0));
  }

  @Test
  void fivePercentThresholdsForTwoEstimatesAreInterpolated() {
    ProjectTimeEstimatesSummary.TimeEstimatesSummary summary =
      new ProjectTimeEstimatesSummary.TimeEstimatesSummary(List.of(20.0, 0.0));

    assertThat(summary.getBottomFivePercent(), closeTo(1.0, 0.000001));
    assertThat(summary.getTopFivePercent(), closeTo(19.0, 0.000001));
  }

  @Test
  void aiAssistedSummaryUsesMaximumEstimateAndIgnoresMissingEstimates() {
    GradeBook book = new GradeBook("test");
    Assignment project = addProject(book, Assignment.ProjectType.AI_ASSISTED);
    noteSubmission(addStudent(book, "student1"), project, 2.0, 4.0);
    noteSubmission(addStudent(book, "student2"), project, 6.0);
    noteSubmission(addStudent(book, "student3"), project, (Double) null);

    ProjectTimeEstimatesSummary.TimeEstimatesSummaries summaries =
      new ProjectTimeEstimatesSummary().getTimeEstimateSummaries(book);
    ProjectTimeEstimatesSummary.TimeEstimatesSummary estimates =
      summaries.getTimeEstimateSummary(Assignment.ProjectType.AI_ASSISTED);

    assertThat(estimates.getCount(), equalTo(2));
    assertThat(estimates.getAverage(), equalTo(5.0));
    StringWriter output = new StringWriter();
    summaries.generateMarkdown(output, List.of(Assignment.ProjectType.AI_ASSISTED));
    assertThat(output.toString(), containsString("|  | AI Assisted |"));
    assertThat(output.toString(), containsString("| Count | 2 |"));
    assertThat(output.toString(), containsString("| Average | 5 hours |"));
  }

  @Test
  void generateMarkdown() {
    GradeBook book = new GradeBook("test");

    Assignment appClasses = addProject(book, "AppClasses", Assignment.ProjectType.APP_CLASSES);
    Assignment textFile = addProject(book, "TextFile", Assignment.ProjectType.TEXT_FILE);
    
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
    ProjectTimeEstimatesSummary.TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    ProjectTimeEstimatesSummary.TimeEstimatesSummary appClassesEstimates = summaries.getTimeEstimateSummary(appClasses.getProjectType());
    assertThat(appClassesEstimates.getCount(), equalTo(5));

    StringWriter sw = new StringWriter();
    summaries.generateMarkdown(sw, List.of(appClasses.getProjectType(), textFile.getProjectType()));
    String markdown = sw.toString();

//    System.out.println(markdown);

    List<String> lines = markdown.lines().collect(Collectors.toList());

    assertThat(lines.get(0), equalTo("|  | App Classes | Text File |"));
    assertThat(lines.get(1), equalTo("| :--- | ---: | ---: |"));
    assertThat(lines.get(2), equalTo("| Count | 5 | 5 |"));
    assertThat(lines.get(3), matchesRegex("\\| Average \\| \\d hours \\| \\d hours \\|"));
    assertThat(lines.get(4), equalTo("| Top 5% | 6 hours | 8 hours |"));
    assertThat(lines.get(5), matchesRegex("\\| Top 25% \\| \\d hours \\| \\d hours \\|"));
    assertThat(lines.get(6), matchesRegex("\\| Median \\| \\d hours \\| \\d hours \\|"));
    assertThat(lines.get(7), matchesRegex("\\| Bottom 25% \\| \\d hours \\| \\d hours \\|"));
    assertThat(lines.get(8), equalTo("| Bottom 5% | 2 hours | 6 hours |"));
  }

  @Test
  void okayToGenerateMarkdownWhenProjectsWithoutType() {
    GradeBook book = new GradeBook("test");
    book.addAssignment(new Assignment("project1", 1.0));
    book.addAssignment(new Assignment("project2", 1.0));
    book.addAssignment(new Assignment("project3", 1.0));

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    ProjectTimeEstimatesSummary.TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);

    StringWriter sw = new StringWriter();
    summaries.generateMarkdown(sw, List.of(Assignment.ProjectType.APP_CLASSES, Assignment.ProjectType.TEXT_FILE));
    String markdown = sw.toString();

    List<String> lines = markdown.lines().collect(Collectors.toList());

    assertThat(lines.get(0), equalTo("|  | App Classes | Text File |"));
    assertThat(lines.get(1), equalTo("| :--- | ---: | ---: |"));
    assertThat(lines.get(2), equalTo("| Count | 0 | 0 |"));
    assertThat(lines.get(3), equalTo("| Average | n/a | n/a |"));
  }

  @Test
  void allProjectTypesAreFormatted() {
    Arrays.stream(Assignment.ProjectType.values()).forEach(projectType -> MatcherAssert.assertThat(ProjectTimeEstimatesSummary.TimeEstimatesSummaries.formatProjectType(projectType), notNullValue()));
  }

  @Test
  void submissionsWithoutEstimatesDoNotCount() {
    GradeBook book = new GradeBook("test");

    Assignment appClasses = addProject(book, "AppClasses", Assignment.ProjectType.APP_CLASSES);
    Student student1 = addStudent(book, "student1");
    noteSubmission(student1, appClasses, (Double) null);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    ProjectTimeEstimatesSummary.TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);
    assertThat(summaries.getTimeEstimateSummary(Assignment.ProjectType.APP_CLASSES), nullValue());
  }

  @Test
  void calculationsUsingMultipleGradeBooks() {
    GradeBook book1 = new GradeBook("Book 1");
    GradeBook book2 = new GradeBook("Book 2");

    Assignment.ProjectType projectType = Assignment.ProjectType.APP_CLASSES;

    Assignment project1 = addProject(book1, projectType);
    noteSubmission(addStudent(book1, "student1"), project1, 1.0);
    noteSubmission(addStudent(book1, "student2"), project1, 2.0);
    Assignment project2 = addProject(book2, projectType);
    noteSubmission(addStudent(book2, "student4"), project2, 4.0);
    noteSubmission(addStudent(book2, "student5"), project2, 5.0);

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    ProjectTimeEstimatesSummary.TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(List.of(book1, book2));
    ProjectTimeEstimatesSummary.TimeEstimatesSummary estimates = summaries.getTimeEstimateSummary(projectType);
    assertThat(estimates.getCount(), equalTo(4));
    assertThat(estimates.getAverage(), equalTo(3.0));
  }
}
