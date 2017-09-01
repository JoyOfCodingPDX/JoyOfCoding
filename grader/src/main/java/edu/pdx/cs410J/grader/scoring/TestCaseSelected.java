package edu.pdx.cs410J.grader.scoring;

public class TestCaseSelected {
  private TestCaseOutput testCaseOutput;

  public TestCaseSelected(TestCaseOutput selected) {
    this.testCaseOutput = selected;
  }

  public TestCaseOutput getTestCaseOutput() {
    return testCaseOutput;
  }

  @Override
  public String toString() {
    return "Selected test case " + testCaseOutput.getName();
  }
}
