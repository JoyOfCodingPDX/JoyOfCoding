package edu.pdx.cs410J;

public class UncaughtExceptionInMain extends RuntimeException {
  UncaughtExceptionInMain(Throwable cause) {
    super("Main methods should not throw exceptions", cause);

  }
}
