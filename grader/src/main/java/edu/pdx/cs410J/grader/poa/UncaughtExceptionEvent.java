package edu.pdx.cs410J.grader.poa;

public class UncaughtExceptionEvent {
  private final Throwable uncaughtException;

  public UncaughtExceptionEvent(Throwable uncaughtException) {
    this.uncaughtException = uncaughtException;
  }

  public Throwable getUncaughtException() {
    return uncaughtException;
  }
}
