package edu.pdx.cs.joy.grader.poa;

import java.time.LocalDateTime;

public class POASubmission {

  private final String subject;
  private final String submitter;
  private final LocalDateTime submitTime;
  private final String content;
  private final String contentType;

  private POASubmission(String subject, String submitter, LocalDateTime submitTime, String content, String contentType) {
    this.subject = subject;
    this.submitter = submitter;
    this.submitTime = submitTime;
    this.content = content;
    this.contentType = contentType;
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
    return String.format("POA from %s with subject %s on %s with %d characters of content",
      getSubmitter(), getSubject(), getSubmitTime(), getContentLength());
  }

  private int getContentLength() {
    String content = getContent();
    if (content == null) {
      return 0;

    } else {
      return content.length();
    }
  }

  public String getContent() {
    return content;
  }

  public String getContentType() {
    return contentType;
  }

  public static class Builder {
    private String subject;
    private String submitter;
    private LocalDateTime submitTime;
    private String content;
    private String contentType;

    public POASubmission create() {
      assertNotNull("subject", subject);
      assertNotNull("submitter", submitter);
      assertNotNull("submitTime", submitTime);
      assertNotNull("content", content);
      assertNotNull("contentType", contentType);

      return new POASubmission(subject, submitter, submitTime, content, contentType);
    }

    private void assertNotNull(String description, Object object) {
      if (object == null) {
        throw new IllegalStateException(description + " cannot be null");
      }
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

    public Builder setContent(String content) {
      this.content = content;
      return this;
    }

    public Builder setContentType(String contentType) {
      this.contentType = contentType;
      return this;
    }
  }
}
