package edu.pdx.cs410J.grader.scoring;

import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ProjectSubmissionXmlConverterTest {

  @Test
  public void convertProjectName() throws JAXBException {
    ProjectSubmission expected = new ProjectSubmission();
    String projectName = "Project";
    expected.setProjectName(projectName);

    ProjectSubmission actual = convertToXmlAndBack(expected);
    assertThat(actual.getProjectName(), equalTo(projectName));
  }

  private ProjectSubmission convertToXmlAndBack(ProjectSubmission submission) throws JAXBException {
    ProjectSubmissionXmlConverter converter = new ProjectSubmissionXmlConverter();
    StringWriter sw = new StringWriter();
    converter.convertToXml(submission, sw);
    String xml = sw.toString();
    System.out.println(xml);
    return converter.convertFromXml(new StringReader(xml));
  }

  @Test
  public void convertStudentId() throws JAXBException {
    ProjectSubmission expected = new ProjectSubmission();
    String studentId = "studentId";
    expected.setStudentId(studentId);

    ProjectSubmission actual = convertToXmlAndBack(expected);
    assertThat(actual.getStudentId(), equalTo(studentId));
  }

  @Test
  public void convertStudentName() throws JAXBException {
    ProjectSubmission expected = new ProjectSubmission();
    String studentName = "studentName";
    expected.setStudentName(studentName);

    ProjectSubmission actual = convertToXmlAndBack(expected);
    assertThat(actual.getStudentName(), equalTo(studentName));
  }

  @Test
  public void convertTotalPoints() throws JAXBException {
    ProjectSubmission expected = new ProjectSubmission();
    double totalPoints = 7.0;
    expected.setTotalPoints(totalPoints);

    ProjectSubmission actual = convertToXmlAndBack(expected);
    assertThat(actual.getTotalPoints(), equalTo(totalPoints));
  }

  @Test
  public void convertNullScore() throws JAXBException {
    ProjectSubmission expected = new ProjectSubmission();
    Double score = null;
    expected.setScore(score);

    ProjectSubmission actual = convertToXmlAndBack(expected);
    assertThat(actual.getScore(), equalTo(score));
  }

  @Test
  public void convertNonNullScore() throws JAXBException {
    ProjectSubmission expected = new ProjectSubmission();
    Double score = 3.4;
    expected.setScore(score);

    ProjectSubmission actual = convertToXmlAndBack(expected);
    assertThat(actual.getScore(), equalTo(score));
  }

  @Test
  public void convertNonSubmissionTime() throws JAXBException {
    ProjectSubmission expected = new ProjectSubmission();
    Date submissionTime = new Date();
    expected.setSubmissionTime(submissionTime);

    ProjectSubmission actual = convertToXmlAndBack(expected);
    assertThat(actual.getSubmissionTime(), equalTo(submissionTime));
  }

  @Test
  public void convertEmptyTestCaseOutput() throws JAXBException {
    ProjectSubmission expected = new ProjectSubmission();
    TestCaseOutput testCase = new TestCaseOutput();
    expected.addTestCaseOutput(testCase);

    ProjectSubmission actual = convertToXmlAndBack(expected);
    List<TestCaseOutput> testCases = actual.getTestCaseOutputs();
    assertThat(testCases.size(), equalTo(1));
  }

  @Test
  public void convertTestCaseOutputPointsDeducted() throws JAXBException {
    ProjectSubmission expected = new ProjectSubmission();
    TestCaseOutput testCase = new TestCaseOutput();
    double pointsDeducted = 3.4;
    testCase.setPointsDeducted(pointsDeducted);
    expected.addTestCaseOutput(testCase);

    ProjectSubmission actual = convertToXmlAndBack(expected);
    List<TestCaseOutput> testCases = actual.getTestCaseOutputs();
    assertThat(testCases.size(), equalTo(1));
    assertThat(testCases.get(0).getPointsDeducted(), equalTo(pointsDeducted));
  }

  @Test
  public void convertTestCaseOutputGraderComment() throws JAXBException {
    ProjectSubmission expected = new ProjectSubmission();
    TestCaseOutput testCase = new TestCaseOutput();
    String graderComment = "This is a comment";
    testCase.setGraderComment(graderComment);
    expected.addTestCaseOutput(testCase);

    ProjectSubmission actual = convertToXmlAndBack(expected);
    List<TestCaseOutput> testCases = actual.getTestCaseOutputs();
    assertThat(testCases.size(), equalTo(1));
    assertThat(testCases.get(0).getGraderComment(), equalTo(graderComment));
  }

  @Test
  public void convertTestCaseOutputDescription() throws JAXBException {
    ProjectSubmission expected = new ProjectSubmission();
    TestCaseOutput testCase = new TestCaseOutput();
    String description = "This is a description";
    testCase.setDescription(description);
    expected.addTestCaseOutput(testCase);

    ProjectSubmission actual = convertToXmlAndBack(expected);
    List<TestCaseOutput> testCases = actual.getTestCaseOutputs();
    assertThat(testCases.size(), equalTo(1));
    assertThat(testCases.get(0).getDescription(), equalTo(description));
  }

  @Test
  public void convertTestCaseCommand() throws JAXBException {
    ProjectSubmission expected = new ProjectSubmission();
    TestCaseOutput testCase = new TestCaseOutput();
    String command = "This is a command";
    testCase.setCommand(command);
    expected.addTestCaseOutput(testCase);

    ProjectSubmission actual = convertToXmlAndBack(expected);
    List<TestCaseOutput> testCases = actual.getTestCaseOutputs();
    assertThat(testCases.size(), equalTo(1));
    assertThat(testCases.get(0).getCommand(), equalTo(command));
  }

  @Test
  public void convertTestCaseName() throws JAXBException {
    ProjectSubmission expected = new ProjectSubmission();
    TestCaseOutput testCase = new TestCaseOutput();
    String name = "This is the name";
    testCase.setName(name);
    expected.addTestCaseOutput(testCase);

    ProjectSubmission actual = convertToXmlAndBack(expected);
    List<TestCaseOutput> testCases = actual.getTestCaseOutputs();
    assertThat(testCases.size(), equalTo(1));
    assertThat(testCases.get(0).getName(), equalTo(name));
  }

  @Test
  public void convertTestCaseOutput() throws JAXBException {
    ProjectSubmission expected = new ProjectSubmission();
    TestCaseOutput testCase = new TestCaseOutput();
    String output = "This is output";
    testCase.setOutput(output);
    expected.addTestCaseOutput(testCase);

    ProjectSubmission actual = convertToXmlAndBack(expected);
    List<TestCaseOutput> testCases = actual.getTestCaseOutputs();
    assertThat(testCases.size(), equalTo(1));
    assertThat(testCases.get(0).getOutput(), equalTo(output));
  }

  @Test
  public void convertMultipleTestCaseOutputs() throws JAXBException {
    String name1 = "Name1";
    String command1 = "Command1";
    Double pointsDeducted1 = 1.0;
    String comment1 = "Comment1";
    String output1 = "This is output1";
    String description1 = "Description1";

    String name2 = "Name2";
    String command2 = "Command2";
    Double pointsDeducted2 = 2.0;
    String comment2 = "Comment2";
    String output2 = "This is output2";
    String description2 = "Description2";

    ProjectSubmission expected = new ProjectSubmission();
    TestCaseOutput testCase1 = new TestCaseOutput()
      .setName(name1)
      .setDescription(description1)
      .setCommand(command1)
      .setOutput(output1)
      .setPointsDeducted(pointsDeducted1)
      .setGraderComment(comment1);
    expected.addTestCaseOutput(testCase1);

    TestCaseOutput testCase2 = new TestCaseOutput()
      .setName(name2)
      .setDescription(description2)
      .setCommand(command2)
      .setOutput(output2)
      .setPointsDeducted(pointsDeducted2)
      .setGraderComment(comment2);
    expected.addTestCaseOutput(testCase2);

    ProjectSubmission actual = convertToXmlAndBack(expected);
    List<TestCaseOutput> testCases = actual.getTestCaseOutputs();
    assertThat(testCases.size(), equalTo(2));
    assertThat(testCases.get(0).getName(), equalTo(name1));
    assertThat(testCases.get(0).getDescription(), equalTo(description1));
    assertThat(testCases.get(0).getCommand(), equalTo(command1));
    assertThat(testCases.get(0).getOutput(), equalTo(output1));
    assertThat(testCases.get(0).getPointsDeducted(), equalTo(pointsDeducted1));
    assertThat(testCases.get(0).getGraderComment(), equalTo(comment1));
    assertThat(testCases.get(1).getName(), equalTo(name2));
    assertThat(testCases.get(1).getDescription(), equalTo(description2));
    assertThat(testCases.get(1).getCommand(), equalTo(command2));
    assertThat(testCases.get(1).getOutput(), equalTo(output2));
    assertThat(testCases.get(1).getPointsDeducted(), equalTo(pointsDeducted2));
    assertThat(testCases.get(1).getGraderComment(), equalTo(comment2));
  }

}
