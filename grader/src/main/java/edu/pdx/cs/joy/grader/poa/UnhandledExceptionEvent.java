package edu.pdx.cs.joy.grader.poa;

public class UnhandledExceptionEvent {
  private final Throwable unhandledException;

  public UnhandledExceptionEvent(Throwable unhandledException) {
    this.unhandledException = unhandledException;
  }

  public Throwable getUnhandledException() {
    return unhandledException;
  }
}
