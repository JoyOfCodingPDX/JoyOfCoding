package edu.pdx.cs410J.grader.poa;

import java.time.LocalDateTime;

public abstract class POASubmissionTestCase extends EventBusTestCase {

  protected POASubmission createPOASubmission(String subject, String submitter, LocalDateTime submitTime) {
    POASubmission.Builder builder = POASubmission.builder();
    builder.setSubject(subject);
    builder.setSubmitter(submitter);
    builder.setSubmitTime(submitTime);
    return builder.create();
  }
}
