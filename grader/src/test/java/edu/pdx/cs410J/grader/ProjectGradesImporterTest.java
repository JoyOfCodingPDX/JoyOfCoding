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
    assertThat(score.getTotalPoints(), equalTo(3.5));
    assertThat(score.getScore(), equalTo(3.4));
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


  // Multiple grades in a file (only first one)
}
