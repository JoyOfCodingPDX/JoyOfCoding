package edu.pdx.cs410J.grader.poa;

public class StatusMessage {
  private final String message;

  public StatusMessage(String message) {
    this.message = message;
  }

  public String getStatusMessage() {
    return this.message;
  }

  @Override
  public String toString() {
    return "Status of \"" + this.message + "\"";
  }
}
