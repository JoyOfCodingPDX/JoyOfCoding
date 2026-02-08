package edu.pdx.cs.joy.grader;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;

import static edu.pdx.cs.joy.grader.TestedProjectSubmissionOutputParser.parseTestedSubmissionOutput;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestedProjectSubmissionOutputParserTest {

  @Test
  public void gradedProjectWithNoGradeThrowsScoreNotFoundException() {
    TestedProjectSubmissionOutput project = new TestedProjectSubmissionOutput();
    project.addLine("asdfhjkl");
    project.addLine("iadguow");

    assertThrows(TestedProjectSubmissionOutputParser.TestedProjectSubmissionOutputParsingException.class, () ->
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
  public void gradedProjectWithOutOfHasValidScore() throws TestedProjectSubmissionOutputParser.TestedProjectSubmissionOutputParsingException, IOException {
    TestedProjectSubmissionOutput project = new TestedProjectSubmissionOutput();
    project.addLine("              The Joy of Coding Project 2: edu.pdx.cs410J.student.Project2");
    project.addLine("3.4 out of 3.5");
    project.addLine("iadguow");

    TestedProjectSubmissionOutputParser.ProjectScore score = parseTestedSubmissionOutput(project.getReader());
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
  public void gradedProjectWithIntegerPoints() throws TestedProjectSubmissionOutputParser.TestedProjectSubmissionOutputParsingException, IOException {
    TestedProjectSubmissionOutput project = new TestedProjectSubmissionOutput();
    project.addLine("              The Joy of Coding Project 2: edu.pdx.cs410J.student.Project2");
    project.addLine("3 out of 5");
    project.addLine("iadguow");

    TestedProjectSubmissionOutputParser.ProjectScore score = parseTestedSubmissionOutput(project.getReader());
    assertThat(score.getScore(), equalTo(3.0));
    assertThat(score.getTotalPoints(), equalTo(5.0));
  }

  static class TestedProjectSubmissionOutput {
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
  public void onlyFirstScoreIsReturned() throws TestedProjectSubmissionOutputParser.TestedProjectSubmissionOutputParsingException, IOException {
    TestedProjectSubmissionOutput project = new TestedProjectSubmissionOutput();
    project.addLine("              The Joy of Coding Project 2: edu.pdx.cs410J.student.Project2");
    project.addLine("3.4 out of 3.5");
    project.addLine("iadguow");
    project.addLine("3.3 out of 3.4");

    TestedProjectSubmissionOutputParser.ProjectScore score = parseTestedSubmissionOutput(project.getReader());
    assertThat(score.getScore(), equalTo(3.4));
    assertThat(score.getTotalPoints(), equalTo(3.5));
  }

  @Test
  public void projectNameIsIdentified() throws TestedProjectSubmissionOutputParser.TestedProjectSubmissionOutputParsingException, IOException {
    String projectName = "Project2";

    TestedProjectSubmissionOutput project = new TestedProjectSubmissionOutput();
    project.addLine("");
    project.addLine("              The Joy of Coding Project 2: edu.pdx.cs410J.student." + projectName);
    project.addLine("              Submitted by Student Name");
    project.addLine("              Submitted on Wed Feb  4 05:07:17 PM PST 2026");
    project.addLine("              Graded on    Wed Feb  4 06:08:07 PM PST 2026");
    project.addLine("");
    project.addLine("5.5 out of 6.0");
    project.addLine("");

    TestedProjectSubmissionOutputParser.ProjectScore score = parseTestedSubmissionOutput(project.getReader());
    assertThat(score.getScore(), equalTo(5.5));
    assertThat(score.getTotalPoints(), equalTo(6.0));
    assertThat(score.getProjectName(), equalTo(projectName));
    assertThat(score.isReviewed(), equalTo(true));
  }

  @Test
  public void scoreRegularExpressionWorksWithMissingScore() {
    Matcher matcher = TestedProjectSubmissionOutputParser.scorePattern.matcher(" out of 4.5");
    assertThat(matcher.find(), equalTo(true));
    assertThat(matcher.groupCount(), equalTo(2));
    assertThat(matcher.group(1), equalTo(null));
    assertThat(matcher.group(2), equalTo("4.5"));
  }

  @Test
  public void ungradedProjectWithoutCommentIsNotReviewed() throws TestedProjectSubmissionOutputParser.TestedProjectSubmissionOutputParsingException, IOException {
    String projectName = "Project2";

    TestedProjectSubmissionOutput project = new TestedProjectSubmissionOutput();
    project.addLine("");
    project.addLine("              The Joy of Coding Project 2: edu.pdx.cs410J.student." + projectName);
    project.addLine("              Submitted by Student Name");
    project.addLine("              Submitted on Wed Feb  4 05:07:17 PM PST 2026");
    project.addLine("              Graded on    Wed Feb  4 06:08:07 PM PST 2026");
    project.addLine("");
    project.addLine(" out of 6.0");
    project.addLine("");

    TestedProjectSubmissionOutputParser.ProjectScore score = parseTestedSubmissionOutput(project.getReader());
    assertThat(score.getScore(), equalTo(Double.NaN));
    assertThat(score.getTotalPoints(), equalTo(6.0));
    assertThat(score.getProjectName(), equalTo(projectName));
    assertThat(score.isReviewed(), equalTo(false));
  }

  @Test
  public void testOutputWithCommentsForStudentIsMarkedAsReviewed() throws TestedProjectSubmissionOutputParser.TestedProjectSubmissionOutputParsingException, IOException {
    String projectName = "Project2";

    TestedProjectSubmissionOutput project = new TestedProjectSubmissionOutput();
    project.addLine("");
    project.addLine("Hey Student Name.  I found some issues with your code.  Please fix them and resubmit.");
    project.addLine("");
    project.addLine("              The Joy of Coding Project 2: edu.pdx.cs410J.student." + projectName);
    project.addLine("              Submitted by Student Name");
    project.addLine("              Submitted on Wed Feb  4 05:07:17 PM PST 2026");
    project.addLine("              Graded on    Wed Feb  4 06:08:07 PM PST 2026");
    project.addLine("");
    project.addLine(" out of 6.0");
    project.addLine("");

    TestedProjectSubmissionOutputParser.ProjectScore score = parseTestedSubmissionOutput(project.getReader());
    assertThat(score.getScore(), equalTo(Double.NaN));
    assertThat(score.getTotalPoints(), equalTo(6.0));
    assertThat(score.getProjectName(), equalTo(projectName));
    assertThat(score.isReviewed(), equalTo(true));
  }
}
