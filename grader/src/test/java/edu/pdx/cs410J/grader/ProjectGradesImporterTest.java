package edu.pdx.cs410J.grader;

import org.junit.Test;

import edu.pdx.cs410J.grader.ProjectGradesImporter.ProjectScore;

import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProjectGradesImporterTest {

  @Test(expected = IllegalStateException.class)
  public void gradedProjectWithNoGradeThrowsIllegalStateException() {
    GradedProject project = new GradedProject();
    project.addLine("asdfhjkl");
    project.addLine("iadguow");

    ProjectGradesImporter.getScoreFrom(project.getReader());
  }

  @Test
  public void scoreRegularExpressionWorkWithSimpleCase() {
    Matcher matcher = ProjectGradesImporter.scorePattern.matcher("3.0 out of 4.5");
    assertThat(matcher.matches(), equalTo(true));
    assertThat(matcher.groupCount(), equalTo(2));
    assertThat(matcher.group(1), equalTo("3.0"));
    assertThat(matcher.group(2), equalTo("4.5"));
  }

  @Test
  public void gradedProjectWithOutOfHasValidScore() {
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
    assertThat(matcher.matches(), equalTo(true));
    assertThat(matcher.groupCount(), equalTo(2));
    assertThat(matcher.group(1), equalTo("4.0"));
    assertThat(matcher.group(2), equalTo("5.0"));
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
  public void onlyFirstScoreIsReturned() {
    GradedProject project = new GradedProject();
    project.addLine("3.4 out of 3.5");
    project.addLine("iadguow");
    project.addLine("3.3 out of 3.4");

    ProjectScore score = ProjectGradesImporter.getScoreFrom(project.getReader());
    assertThat(score.getScore(), equalTo(3.4));
    assertThat(score.getTotalPoints(), equalTo(3.5));
  }

  @Test
  public void scoreIsRecordedInGradeBook() {
    String studentId = "student";
    Assignment assignment = new Assignment("project", 6.0);

    GradeBook gradeBook = new GradeBook("test");
    gradeBook.addStudent(new Student(studentId));
    gradeBook.addAssignment(assignment);

    GradedProject project = new GradedProject();
    project.addLine("5.8 out of 6.0");
    project.addLine("");
    project.addLine("asdfasd");

    ProjectGradesImporter importer = new ProjectGradesImporter(gradeBook, assignment);
    importer.recordScoreFromProjectReport(studentId, project.getReader());

    assertThat(gradeBook.getStudent(studentId).getGrade(assignment.getName()).getScore(), equalTo(5.8));
    assertThat(gradeBook.isDirty(), equalTo(true));
  }

  // Total points doesn't match total in grade book

}
