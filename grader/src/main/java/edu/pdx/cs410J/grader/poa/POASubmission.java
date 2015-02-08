package edu.pdx.cs410J.grader.poa;

import java.time.LocalDateTime;

public class POASubmission {

  private final String subject;
  private final String submitter;
  private final LocalDateTime submitTime;

  private POASubmission(String subject, String submitter, LocalDateTime submitTime) {
    this.subject = subject;
    this.submitter = submitter;
    this.submitTime = submitTime;
  }

  public static Builder builder() {
    return new Builder();
  }

  public String getSubject() {
    return subject;
  }

  public String getSubmitter() {
    return submitter;
  }

  public LocalDateTime getSubmitTime() {
    return submitTime;
  }

  @Override
  public String toString() {
    return String.format("POA from %s with subject %s on %s", getSubmitter(), getSubject(), getSubmitTime());
  }

  public static class Builder {
    private String subject;
    private String submitter;
    private LocalDateTime submitTime;

    public POASubmission create() {
      return new POASubmission(subject, submitter, submitTime);
    }

    public Builder setSubject(String subject) {
      this.subject = subject;
      return this;
    }

    public Builder setSubmitter(String submitter) {
      this.submitter = submitter;
      return this;
    }

    public Builder setSubmitTime(LocalDateTime submitTime) {
      this.submitTime = submitTime;
      return this;
    }
  }
}
