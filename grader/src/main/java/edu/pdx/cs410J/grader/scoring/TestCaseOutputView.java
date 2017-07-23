package edu.pdx.cs410J.grader.scoring;

public interface TestCaseOutputView {
  void setDescription(String description);

  void setCommand(String command);

  void setOutput(String output);

  void setPointsDeducted(String pointsDeducted);
}
