package edu.pdx.cs410J.grader.poa;

import java.time.LocalDateTime;

public class POASubmission {

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String subject;
    private String submitter;
    private LocalDateTime submitTime;

    public POASubmission create() {
      return null;
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
