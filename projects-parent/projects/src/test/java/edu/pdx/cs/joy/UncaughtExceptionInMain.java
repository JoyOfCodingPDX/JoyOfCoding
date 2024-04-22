package edu.pdx.cs.joy;

public class UncaughtExceptionInMain extends RuntimeException {
  UncaughtExceptionInMain(Throwable cause) {
    super("Main methods should not throw exceptions", cause);

  }
}
