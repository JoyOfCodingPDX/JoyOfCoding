package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.grader.ProjectGradesImporter.ProjectScore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

public class ProjectGradesImporterTest {

  private Logger logger = LoggerFactory.getLogger(this.getClass().getPackage().getName());

  @Test(expected = ProjectGradesImporter.ScoreNotFoundException.class)
  public void gradedProjectWithNoGradeThrowsScoreNotFoundException() throws ProjectGradesImporter.ScoreNotFoundException {
    GradedProject project = new GradedProject();
    project.addLine("asdfhjkl");
    project.addLine("iadguow");

    ProjectGradesImporter.getScoreFrom(project.getReader());
  }

  @Test
  public void scoreRegularExpressionWorkWithSimpleCase() {
    Matcher matcher = ProjectGradesImporter.scorePattern.matcher("3.0 out of 4.5");
    assertThat(matcher.find(), equalTo(true));
    assertThat(matcher.groupCount(), equalTo(2));
    assertThat(matcher.group(1), equalTo("3.0"));
    assertThat(matcher.group(2), equalTo("4.5"));
  }

  @Test
  public void gradedProjectWithOutOfHasValidScore() throws ProjectGradesImporter.ScoreNotFoundException {
    GradedProject project = new GradedProject();
    project.addLine("3.4 out of 3.5");
    project.addLine("iadguow");

    ProjectScore score = ProjectGradesImporter.getScoreFrom(project.getReader());
    assertThat(score.getScore(), equalTo(3.4));
    assertThat(score.getTotalPoints(), equalTo(3.5));
  }

  @Test
  public void scoreMatchesRegardlessOfCase() {
    Matcher matcher = ProjectGradesImporter.scorePattern.matcher("4.0 OUT OF 5.0");
    assertThat(matcher.find(), equalTo(true));
    assertThat(matcher.groupCount(), equalTo(2));
    assertThat(matcher.group(1), equalTo("4.0"));
    assertThat(matcher.group(2), equalTo("5.0"));
  }

  @Test
  public void scoreMatchesIntegerPoints() {
    Matcher matcher = ProjectGradesImporter.scorePattern.matcher("4 out of 5");
    assertThat(matcher.find(), equalTo(true));
    assertThat(matcher.groupCount(), equalTo(2));
    assertThat(matcher.group(1), equalTo("4"));
    assertThat(matcher.group(2), equalTo("5"));
  }

  @Test
  public void scoreMatchesInTheMiddleOfOtherText() {
    Matcher matcher = ProjectGradesImporter.scorePattern.matcher("You got 4 out of 5 points");
    assertThat(matcher.find(), equalTo(true));
    assertThat(matcher.groupCount(), equalTo(2));
    assertThat(matcher.group(1), equalTo("4"));
    assertThat(matcher.group(2), equalTo("5"));
  }

  @Test
  public void gradedProjectWithIntegerPoints() throws ProjectGradesImporter.ScoreNotFoundException {
    GradedProject project = new GradedProject();
    project.addLine("3 out of 5");
    project.addLine("iadguow");

    ProjectScore score = ProjectGradesImporter.getScoreFrom(project.getReader());
    assertThat(score.getScore(), equalTo(3.0));
    assertThat(score.getTotalPoints(), equalTo(5.0));
  }

  private class GradedProject {
    private StringBuilder sb = new StringBuilder();

    public void addLine(String line) {
      sb.append(line);
      sb.append("\n");
    }

    public Reader getReader() {
      return new StringReader(sb.toString());
    }
  }

  @Test
  public void onlyFirstScoreIsReturned() throws ProjectGradesImporter.ScoreNotFoundException {
    GradedProject project = new GradedProject();
    project.addLine("3.4 out of 3.5");
    project.addLine("iadguow");
    project.addLine("3.3 out of 3.4");

    ProjectScore score = ProjectGradesImporter.getScoreFrom(project.getReader());
    assertThat(score.getScore(), equalTo(3.4));
    assertThat(score.getTotalPoints(), equalTo(3.5));
  }

  @Test
  public void scoreIsRecordedInGradeBook() throws ProjectGradesImporter.ScoreNotFoundException {
    String studentId = "student";
    Assignment assignment = new Assignment("project", 6.0);

    GradeBook gradeBook = new GradeBook("test");
    gradeBook.addStudent(new Student(studentId));
    gradeBook.addAssignment(assignment);

    String score = "5.8";
    GradedProject project = new GradedProject();
    project.addLine(score + " out of 6.0");
    project.addLine("");
    project.addLine("asdfasd");

    Logger logger = mock(Logger.class);
    ProjectGradesImporter importer = new ProjectGradesImporter(gradeBook, assignment, logger);
    importer.recordScoreFromProjectReport(studentId, project.getReader());

    assertThat(gradeBook.getStudent(studentId).get().getGrade(assignment.getName()).getScore(), equalTo(5.8));
    assertThat(gradeBook.isDirty(), equalTo(true));

    ArgumentCaptor<String> message = ArgumentCaptor.forClass(String.class);
    verify(logger).info(message.capture());
    assertThat(message.getValue(), containsString("Recorded grade of " + score + " for " + studentId));
  }

  @Test(expected = IllegalStateException.class)
  public void throwIllegalStateExceptionWhenTotalPointsInReportDoesNotMatchGradeBook() throws ProjectGradesImporter.ScoreNotFoundException {
    String studentId = "student";
    Assignment assignment = new Assignment("project", 8.0);

    GradeBook gradeBook = new GradeBook("test");
    gradeBook.addStudent(new Student(studentId));
    gradeBook.addAssignment(assignment);

    GradedProject project = new GradedProject();
    project.addLine("5.8 out of 6.0");
    project.addLine("");
    project.addLine("asdfasd");

    ProjectGradesImporter importer = new ProjectGradesImporter(gradeBook, assignment, logger);
    importer.recordScoreFromProjectReport(studentId, project.getReader());
  }

  @Test
  public void logWarningWhenStudentDoesNotExistInGradeBook() throws ProjectGradesImporter.ScoreNotFoundException {
    String studentId = "student";
    Assignment assignment = new Assignment("project", 6.0);

    GradeBook gradeBook = new GradeBook("test");
    gradeBook.addAssignment(assignment);

    GradedProject project = new GradedProject();
    project.addLine("5.8 out of 6.0");
    project.addLine("");
    project.addLine("asdfasd");

    Logger logger = mock(Logger.class);
    ProjectGradesImporter importer = new ProjectGradesImporter(gradeBook, assignment, logger);

    importer.recordScoreFromProjectReport(studentId, project.getReader());

    assertThat(gradeBook.containsStudent(studentId), equalTo(false));

    ArgumentCaptor<String> message = ArgumentCaptor.forClass(String.class);
    verify(logger, times(1)).warn(message.capture());
    assertThat(message.getValue(), containsString("Student \"" + studentId + "\" not found in gradebook"));
  }

}
