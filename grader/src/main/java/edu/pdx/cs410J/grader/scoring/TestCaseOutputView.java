package edu.pdx.cs410J.grader.scoring;

public interface TestCaseOutputView {
  void setDescription(String description);

  void setCommand(String command);

  void setOutput(String output);

  void setPointsDeducted(String pointsDeducted);

  void addGraderCommentChangeListener(GraderCommentChangeListener listener);

  void setGraderComment(String comment);

  void addPointsDeductedChangeListener(PointsDeductedChangeListener listener);

  void setPointsDeductedIsValid(boolean isValidPointsDeducted);

  interface GraderCommentChangeListener {
    void onGraderCommentChange(String comment);
  }

  interface PointsDeductedChangeListener {
    void onPointsDeductedChange(String pointsDeducted);
  }
}
