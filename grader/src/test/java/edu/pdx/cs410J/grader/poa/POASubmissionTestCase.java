package edu.pdx.cs410J.grader.poa;

import edu.pdx.cs410J.grader.mvp.EventBusTestCase;

import java.time.LocalDateTime;

public abstract class POASubmissionTestCase extends EventBusTestCase {

  protected POASubmission createPOASubmission(String subject, String submitter, LocalDateTime submitTime) {
    return createPOASubmission(subject, submitter, submitTime, "Content");
  }

  protected POASubmission createPOASubmission(String subject, String submitter, LocalDateTime submitTime, String content) {
    POASubmission.Builder builder = POASubmission.builder();
    builder.setSubject(subject);
    builder.setSubmitter(submitter);
    builder.setSubmitTime(submitTime);
    builder.setContent(content);
    return builder.create();
  }
}
