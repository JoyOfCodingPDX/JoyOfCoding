package edu.pdx.cs410J.grader.mvp;

public interface UnhandledExceptionView {

  void setExceptionMessage(String message);

  void setExceptionDetails(String details);

  void displayView();
}
