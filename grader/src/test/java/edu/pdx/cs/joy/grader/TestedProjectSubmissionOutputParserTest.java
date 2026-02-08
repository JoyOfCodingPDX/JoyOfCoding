package edu.pdx.cs.joy.grader;

import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestedProjectSubmissionOutputParserTest {

  @Test
  public void gradedProjectWithNoGradeThrowsScoreNotFoundException() {
    GradedProject project = new GradedProject();
    project.addLine("asdfhjkl");
    project.addLine("iadguow");

    assertThrows(TestedProjectSubmissionOutputParser.ScoreNotFoundException.class, () ->
      ProjectGradesImporter.getScoreFrom(project.getReader())
    );
  }

  @Test
  public void scoreRegularExpressionWorkWithSimpleCase() {
    Matcher matcher = TestedProjectSubmissionOutputParser.scorePattern.matcher("3.0 out of 4.5");
    assertThat(matcher.find(), equalTo(true));
    assertThat(matcher.groupCount(), equalTo(2));
    assertThat(matcher.group(1), equalTo("3.0"));
    assertThat(matcher.group(2), equalTo("4.5"));
  }

  @Test
  public void gradedProjectWithOutOfHasValidScore() throws TestedProjectSubmissionOutputParser.ScoreNotFoundException {
    GradedProject project = new GradedProject();
    project.addLine("3.4 out of 3.5");
    project.addLine("iadguow");

    TestedProjectSubmissionOutputParser.ProjectScore score = TestedProjectSubmissionOutputParser.parseTestedSubmissionOutput(project.getReader());
    assertThat(score.getScore(), equalTo(3.4));
    assertThat(score.getTotalPoints(), equalTo(3.5));
  }

  @Test
  public void scoreMatchesRegardlessOfCase() {
    Matcher matcher = TestedProjectSubmissionOutputParser.scorePattern.matcher("4.0 OUT OF 5.0");
    assertThat(matcher.find(), equalTo(true));
    assertThat(matcher.groupCount(), equalTo(2));
    assertThat(matcher.group(1), equalTo("4.0"));
    assertThat(matcher.group(2), equalTo("5.0"));
  }

  @Test
  public void scoreMatchesIntegerPoints() {
    Matcher matcher = TestedProjectSubmissionOutputParser.scorePattern.matcher("4 out of 5");
    assertThat(matcher.find(), equalTo(true));
    assertThat(matcher.groupCount(), equalTo(2));
    assertThat(matcher.group(1), equalTo("4"));
    assertThat(matcher.group(2), equalTo("5"));
  }

  @Test
  public void scoreMatchesInTheMiddleOfOtherText() {
    Matcher matcher = TestedProjectSubmissionOutputParser.scorePattern.matcher("You got 4 out of 5 points");
    assertThat(matcher.find(), equalTo(true));
    assertThat(matcher.groupCount(), equalTo(2));
    assertThat(matcher.group(1), equalTo("4"));
    assertThat(matcher.group(2), equalTo("5"));
  }

  @Test
  public void gradedProjectWithIntegerPoints() throws TestedProjectSubmissionOutputParser.ScoreNotFoundException {
    GradedProject project = new GradedProject();
    project.addLine("3 out of 5");
    project.addLine("iadguow");

    TestedProjectSubmissionOutputParser.ProjectScore score = TestedProjectSubmissionOutputParser.parseTestedSubmissionOutput(project.getReader());
    assertThat(score.getScore(), equalTo(3.0));
    assertThat(score.getTotalPoints(), equalTo(5.0));
  }

  static class GradedProject {
    private final StringBuilder sb = new StringBuilder();

    public void addLine(String line) {
      sb.append(line);
      sb.append("\n");
    }

    public Reader getReader() {
      return new StringReader(sb.toString());
    }
  }

  @Test
  public void onlyFirstScoreIsReturned() throws TestedProjectSubmissionOutputParser.ScoreNotFoundException {
    GradedProject project = new GradedProject();
    project.addLine("3.4 out of 3.5");
    project.addLine("iadguow");
    project.addLine("3.3 out of 3.4");

    TestedProjectSubmissionOutputParser.ProjectScore score = TestedProjectSubmissionOutputParser.parseTestedSubmissionOutput(project.getReader());
    assertThat(score.getScore(), equalTo(3.4));
    assertThat(score.getTotalPoints(), equalTo(3.5));
  }
}
