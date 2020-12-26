package edu.pdx.cs410J.grader.poa;

import java.time.LocalDateTime;

public abstract class POASubmissionTestCase extends EventBusTestCase {

  protected POASubmission createPOASubmission(String subject, String submitter, LocalDateTime submitTime) {
    return createPOASubmission(subject, submitter, submitTime, "Content", "text/plain");
  }

  protected POASubmission createPOASubmission(String subject, String submitter, LocalDateTime submitTime, String content, String contentType) {
    POASubmission.Builder builder = POASubmission.builder();
    builder.setSubject(subject);
    builder.setSubmitter(submitter);
    builder.setSubmitTime(submitTime);
    builder.setContent(content);
    builder.setContentType(contentType);
    return builder.create();
  }
}
