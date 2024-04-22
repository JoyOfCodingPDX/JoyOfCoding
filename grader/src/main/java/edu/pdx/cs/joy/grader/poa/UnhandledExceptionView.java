package edu.pdx.cs.joy.grader.poa;

public interface UnhandledExceptionView {

  void setExceptionMessage(String message);

  void setExceptionDetails(String details);

  void displayView();
}
