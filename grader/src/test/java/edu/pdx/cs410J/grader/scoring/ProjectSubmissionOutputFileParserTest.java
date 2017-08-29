package edu.pdx.cs410J.grader.scoring;

import org.junit.Ignore;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ProjectSubmissionOutputFileParserTest {

  /*

              CS410J Project 3: edu.pdx.cs410J.whitlock.Project3
              Submitted by David Whitlock
              Submitted on 2017-Jul-28 19:51:41
              Graded on Fri Jul 28 19:53:58 PDT 2017

 out of 8.0

   */

  @Test
  public void parseProjectName() {
    OutputFile file = new OutputFile();
    file.line("              CS410J Project 3: edu.pdx.cs410J.whitlock.Project3");

    ProjectSubmission submission = parse(file);
    assertThat(submission.getProjectName(), equalTo("Project3"));
  }

  @Test
  public void parseStudentId() {
    OutputFile file = new OutputFile();
    file.line("              CS410J Project 3: edu.pdx.cs410J.whitlock.Project3");

    ProjectSubmission submission = parse(file);
    assertThat(submission.getStudentId(), equalTo("whitlock"));
  }

  @Test
  public void parseStudentName() {
    OutputFile file = new OutputFile();
    file.line("              CS410J Project 3: edu.pdx.cs410J.whitlock.Project3");
    file.line("              Submitted by David Whitlock");

    ProjectSubmission submission = parse(file);
    assertThat(submission.getStudentName(), equalTo("David Whitlock"));
  }

  @Test
  public void parseSubmissionTime() throws ParseException {
    OutputFile file = new OutputFile();
    String submissionTimeString = "2017-Jul-28 19:51:41";

    file.line("              CS410J Project 3: edu.pdx.cs410J.whitlock.Project3");
    file.line("              Submitted by David Whitlock");
    file.line("              Submitted on " + submissionTimeString + "\n");

    ProjectSubmission submission = parse(file);
    Date submissionTime = ProjectSubmissionOutputFileParser.parseSubmissionTime(submissionTimeString);
    assertThat(submission.getSubmissionTime(), equalTo(submissionTime));
  }

  @Test

  public void parseGradingTime() throws ParseException {
    OutputFile file = new OutputFile();
    String gradingTimeString = "Fri Jul 28 19:53:58 PDT 2017";

    file.line("              CS410J Project 3: edu.pdx.cs410J.whitlock.Project3");
    file.line("              Submitted by David Whitlock");
    file.line("              Submitted on 2017-Jul-28 19:51:41\n");
    file.line("              Graded on " + gradingTimeString + "\n");

    ProjectSubmission submission = parse(file);
    Date gradingTime = ProjectSubmissionOutputFileParser.parseGradingTime(gradingTimeString);
    assertThat(submission.getGradedTime(), equalTo(gradingTime));
  }


  @Test
  public void parseTotalPoints() {
    OutputFile file = new OutputFile();

    file.line("              CS410J Project 3: edu.pdx.cs410J.whitlock.Project3");
    file.line("              Submitted by David Whitlock");
    file.line("              Submitted on 2017-Jul-28 19:51:41\n");
    file.line("              Graded on Fri Jul 28 19:53:58 PDT 2017\n");
    file.line("");
    file.line(" out of 8.0");

    ProjectSubmission submission = parse(file);
    assertThat(submission.getTotalPoints(), equalTo(8.0));
  }


  @Test
  public void parseEmptyGrade() {
    OutputFile file = new OutputFile();

    file.line("              CS410J Project 3: edu.pdx.cs410J.whitlock.Project3");
    file.line("              Submitted by David Whitlock");
    file.line("              Submitted on 2017-Jul-28 19:51:41\n");
    file.line("              Graded on Fri Jul 28 19:53:58 PDT 2017\n");
    file.line("");
    file.line(" out of 8.0");

    ProjectSubmission submission = parse(file);
    assertThat(submission.getScore(), equalTo(null));
  }


  @Test
  public void parseSpecifiedGrade() {
    OutputFile file = new OutputFile();

    file.line("              CS410J Project 3: edu.pdx.cs410J.whitlock.Project3");
    file.line("              Submitted by David Whitlock");
    file.line("              Submitted on 2017-Jul-28 19:51:41\n");
    file.line("              Graded on Fri Jul 28 19:53:58 PDT 2017\n");
    file.line("");
    file.line("7.0 out of 8.0");

    ProjectSubmission submission = parse(file);
    assertThat(submission.getScore(), equalTo(7.0));
  }

  @Ignore
  @Test
  public void parseCompilerTestCaseHasSameNameAndDescription() {
    OutputFile file = new OutputFile();
    String testNameAndDescription = "Compiling source code";
    String command = "mvn --quiet --batch-mode --file airline-whitlock/pom.xml -DskipTests package";
    String output = "This is the compiler output";

    file.line("              CS410J Project 3: edu.pdx.cs410J.whitlock.Project3");
    file.line("              Submitted by David Whitlock");
    file.line("              Submitted on 2017-Jul-28 19:51:41\n");
    file.line("              Graded on Fri Jul 28 19:53:58 PDT 2017\n");
    file.line();
    file.line("7.0 out of 8.0");
    file.line("*****  " + testNameAndDescription);
    file.line();
    file.line(command);
    file.line();
    file.line(output);

    ProjectSubmission submission = parse(file);
    List<TestCaseOutput> testCases = submission.getTestCaseOutputs();
    assertThat(testCases.size(), equalTo(1));

    TestCaseOutput testCase = testCases.get(0);
    assertThat(testCase.getName(), equalTo(testNameAndDescription));
    assertThat(testCase.getDescription(), equalTo(testNameAndDescription));
    assertThat(testCase.getCommand(), equalTo(command));
    assertThat(testCase.getOutput(), equalTo(output));
  }

  @Ignore
  @Test
  public void parseCompilerTestWithNoOutput() {
    OutputFile file = new OutputFile();
    String testNameAndDescription = "Compiling source code";
    String command = "mvn --quiet --batch-mode --file airline-whitlock/pom.xml -DskipTests package";

    file.line("              CS410J Project 3: edu.pdx.cs410J.whitlock.Project3");
    file.line("              Submitted by David Whitlock");
    file.line("              Submitted on 2017-Jul-28 19:51:41\n");
    file.line("              Graded on Fri Jul 28 19:53:58 PDT 2017\n");
    file.line();
    file.line("7.0 out of 8.0");
    file.line("*****  " + testNameAndDescription);
    file.line();
    file.line(command);
    file.line();
    file.line("*****  Test 1: No arguments");

    ProjectSubmission submission = parse(file);
    List<TestCaseOutput> testCases = submission.getTestCaseOutputs();
    assertThat(testCases.size(), equalTo(2));

    TestCaseOutput testCase = testCases.get(0);
    assertThat(testCase.getName(), equalTo(testNameAndDescription));
    assertThat(testCase.getDescription(), equalTo(testNameAndDescription));
    assertThat(testCase.getCommand(), equalTo(command));
    assertThat(testCase.getOutput(), equalTo(""));
  }

  @Ignore
  @Test
  public void parseTest1() {
    OutputFile file = new OutputFile();
    String testName = "Test 1";
    String description = "No arguments";
    String command = "$ java -Dstudent.name=np4 -Djava.security.manager -Djava.security.policy=file:/u/sjavata/submissions/summer2017/Project3/student-np4/java.policy -jar airline-np4/target/airline-np4-Summer2017.jar";
    String output = "Please Check! Some of the arguments are missing.";

    file.line("              CS410J Project 3: edu.pdx.cs410J.whitlock.Project3");
    file.line("              Submitted by David Whitlock");
    file.line("              Submitted on 2017-Jul-28 19:51:41\n");
    file.line("              Graded on Fri Jul 28 19:53:58 PDT 2017\n");
    file.line();
    file.line("7.0 out of 8.0");
    file.line("*****  " + "Compiling source code");
    file.line();
    file.line("mvn --quiet --batch-mode --file airline-whitlock/pom.xml -DskipTests package");
    file.line();
    file.line("*****  " + testName + ": " + description);
    file.line();
    file.line(command);
    file.line();
    file.line(output);

    ProjectSubmission submission = parse(file);
    List<TestCaseOutput> testCases = submission.getTestCaseOutputs();
    assertThat(testCases.size(), equalTo(2));

    TestCaseOutput testCase = testCases.get(1);
    assertThat(testCase.getName(), equalTo(testName));
    assertThat(testCase.getDescription(), equalTo(description));
    assertThat(testCase.getCommand(), equalTo(command));
    assertThat(testCase.getOutput(), equalTo(output));
  }


  private ProjectSubmission parse(OutputFile file) {
    String text = file.getText();
    ProjectSubmissionOutputFileParser parser = new ProjectSubmissionOutputFileParser(new StringReader(text));
    return parser.parse();
  }

  private class OutputFile {
    private final StringWriter writer = new StringWriter();
    private final PrintWriter pw = new PrintWriter(writer, true);

    public void line(String line) {
      pw.println(line);
    }

    public String getText() {
      return this.writer.toString();
    }

    public void line() {
      line("");
    }
  }
}
