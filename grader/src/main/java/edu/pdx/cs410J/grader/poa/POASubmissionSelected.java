package edu.pdx.cs410J.grader.poa;

public class POASubmissionSelected {
  private final POASubmission submission;

  public POASubmissionSelected(POASubmission submission) {
    this.submission = submission;
  }

  public POASubmission getSubmission() {
    return submission;
  }

  @Override
  public String toString() {
    return "Submission selected: " + getSubmission();
  }
}
