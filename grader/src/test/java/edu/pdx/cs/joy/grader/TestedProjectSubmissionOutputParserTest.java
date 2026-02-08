package edu.pdx.cs.joy.grader;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.regex.Matcher;

import static edu.pdx.cs.joy.grader.TestedProjectSubmissionOutputParser.*;
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

    assertThrows(TestedProjectSubmissionOutputParsingException.class, () ->
      ProjectGradesImporter.getScoreFrom(project.getReader())
    );
  }

  @Test
  public void scoreRegularExpressionWorkWithSimpleCase() {
    Matcher matcher = scorePattern.matcher("3.0 out of 4.5");
    assertThat(matcher.find(), equalTo(true));
    assertThat(matcher.groupCount(), equalTo(2));
    assertThat(matcher.group(1), equalTo("3.0"));
    assertThat(matcher.group(2), equalTo("4.5"));
  }

  @Test
  public void gradedProjectWithOutOfHasValidScore() throws TestedProjectSubmissionOutputParsingException, IOException {
    TestedProjectSubmissionOutput project = new TestedProjectSubmissionOutput();
    project.addLine("              The Joy of Coding Project 2: edu.pdx.cs410J.student.Project2");
    project.addLine("              Submitted on Wed Feb  4 05:07:17 PM PST 2026");
    project.addLine("3.4 out of 3.5");
    project.addLine("iadguow");

    ProjectScore score = parseTestedSubmissionOutput(project.getReader());
    assertThat(score.getScore(), equalTo(3.4));
    assertThat(score.getTotalPoints(), equalTo(3.5));
  }

  @Test
  public void scoreMatchesRegardlessOfCase() {
    Matcher matcher = scorePattern.matcher("4.0 OUT OF 5.0");
    assertThat(matcher.find(), equalTo(true));
    assertThat(matcher.groupCount(), equalTo(2));
    assertThat(matcher.group(1), equalTo("4.0"));
    assertThat(matcher.group(2), equalTo("5.0"));
  }

  @Test
  public void scoreMatchesIntegerPoints() {
    Matcher matcher = scorePattern.matcher("4 out of 5");
    assertThat(matcher.find(), equalTo(true));
    assertThat(matcher.groupCount(), equalTo(2));
    assertThat(matcher.group(1), equalTo("4"));
    assertThat(matcher.group(2), equalTo("5"));
  }

  @Test
  public void scoreMatchesInTheMiddleOfOtherText() {
    Matcher matcher = scorePattern.matcher("You got 4 out of 5 points");
    assertThat(matcher.find(), equalTo(true));
    assertThat(matcher.groupCount(), equalTo(2));
    assertThat(matcher.group(1), equalTo("4"));
    assertThat(matcher.group(2), equalTo("5"));
  }

  @Test
  public void gradedProjectWithIntegerPoints() throws TestedProjectSubmissionOutputParsingException, IOException {
    TestedProjectSubmissionOutput project = new TestedProjectSubmissionOutput();
    project.addLine("              The Joy of Coding Project 2: edu.pdx.cs410J.student.Project2");
    project.addLine("              Submitted on Wed Feb  4 05:07:17 PM PST 2026");
    project.addLine("3 out of 5");
    project.addLine("iadguow");

    ProjectScore score = parseTestedSubmissionOutput(project.getReader());
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
  public void onlyFirstScoreIsReturned() throws TestedProjectSubmissionOutputParsingException, IOException {
    TestedProjectSubmissionOutput project = new TestedProjectSubmissionOutput();
    project.addLine("              The Joy of Coding Project 2: edu.pdx.cs410J.student.Project2");
    project.addLine("              Submitted on Wed Feb  4 05:07:17 PM PST 2026");
    project.addLine("3.4 out of 3.5");
    project.addLine("iadguow");
    project.addLine("3.3 out of 3.4");

    ProjectScore score = parseTestedSubmissionOutput(project.getReader());
    assertThat(score.getScore(), equalTo(3.4));
    assertThat(score.getTotalPoints(), equalTo(3.5));
  }

  @Test
  public void projectNameIsIdentified() throws TestedProjectSubmissionOutputParsingException, IOException {
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

    ProjectScore score = parseTestedSubmissionOutput(project.getReader());
    assertThat(score.getScore(), equalTo(5.5));
    assertThat(score.getTotalPoints(), equalTo(6.0));
    assertThat(score.getProjectName(), equalTo(projectName));
    assertThat(score.isReviewed(), equalTo(true));
  }

  @Test
  public void scoreRegularExpressionWorksWithMissingScore() {
    Matcher matcher = scorePattern.matcher(" out of 4.5");
    assertThat(matcher.find(), equalTo(true));
    assertThat(matcher.groupCount(), equalTo(2));
    assertThat(matcher.group(1), equalTo(null));
    assertThat(matcher.group(2), equalTo("4.5"));
  }

  @Test
  public void ungradedProjectWithoutCommentIsNotReviewed() throws TestedProjectSubmissionOutputParsingException, IOException {
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

    ProjectScore score = parseTestedSubmissionOutput(project.getReader());
    assertThat(score.getScore(), equalTo(Double.NaN));
    assertThat(score.getTotalPoints(), equalTo(6.0));
    assertThat(score.getProjectName(), equalTo(projectName));
    assertThat(score.isReviewed(), equalTo(false));
  }

  @Test
  public void testOutputWithCommentsForStudentIsMarkedAsReviewed() throws TestedProjectSubmissionOutputParsingException, IOException {
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

    ProjectScore score = parseTestedSubmissionOutput(project.getReader());
    assertThat(score.getScore(), equalTo(Double.NaN));
    assertThat(score.getTotalPoints(), equalTo(6.0));
    assertThat(score.getProjectName(), equalTo(projectName));
    assertThat(score.isReviewed(), equalTo(true));
  }


  @Test
  void parseSubmissionTimeFromAndroidProjectTestOutputLine() {
    LocalDateTime submissionTime = parseSubmissionTime("              Submitted on 2025-08-18T11:34:19.017953486");
    LocalDateTime expectedTime = LocalDateTime.of(2025, 8, 18, 11, 34, 19, 17953486);
    assertThat(submissionTime, equalTo(expectedTime));
  }

  @Test
  void parseSubmissionTimeFromTestOutputLine() {
    LocalDateTime submissionTime = parseSubmissionTime("              Submitted on Wed Aug  6 01:13:59 PM PDT 2025");
    LocalDateTime expectedTime = LocalDateTime.of(2025, 8, 6, 13, 13, 59);
    assertThat(submissionTime, equalTo(expectedTime));
  }

  @Test
  void parseSubmissionTimeFromTestOutputLineWithTwoDigitDay() {
    LocalDateTime submissionTime = parseSubmissionTime("              Submitted on Wed Jul 23 12:59:13 PM PDT 2025");
    LocalDateTime expectedTime = LocalDateTime.of(2025, 7, 23, 12, 59, 13);
    assertThat(submissionTime, equalTo(expectedTime));
  }

  @Test
  public void submissionTimeIsIdentified() throws TestedProjectSubmissionOutputParsingException, IOException {
    String projectName = "Project2";
    LocalDateTime submissionTime = LocalDateTime.of(2026, 2, 4, 17, 7, 17);

    TestedProjectSubmissionOutput project = new TestedProjectSubmissionOutput();
    project.addLine("");
    project.addLine("              The Joy of Coding Project 2: edu.pdx.cs410J.student." + projectName);
    project.addLine("              Submitted by Student Name");
    project.addLine("              Submitted on Wed Feb  4 05:07:17 PM PST 2026");
    project.addLine("              Graded on    Wed Feb  4 06:08:07 PM PST 2026");
    project.addLine("");
    project.addLine("5.5 out of 6.0");
    project.addLine("");

    ProjectScore score = parseTestedSubmissionOutput(project.getReader());
    assertThat(score.getScore(), equalTo(5.5));
    assertThat(score.getTotalPoints(), equalTo(6.0));
    assertThat(score.getProjectName(), equalTo(projectName));
    assertThat(score.isReviewed(), equalTo(true));
    assertThat(score.getSubmissionTime(), equalTo(submissionTime));
  }
}
