package edu.pdx.cs410J.grader.mvp;

public class UnhandledExceptionEvent {
  private final Throwable unhandledException;

  public UnhandledExceptionEvent(Throwable unhandledException) {
    this.unhandledException = unhandledException;
  }

  public Throwable getUnhandledException() {
    return unhandledException;
  }
}
