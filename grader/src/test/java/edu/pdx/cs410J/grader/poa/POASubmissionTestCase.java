package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import org.junit.Before;

import java.time.LocalDateTime;

public abstract class POASubmissionTestCase {
  protected EventBus bus;

  @Before
  public void setUp() {
    bus = new EventBus();
  }

  protected POASubmission createPOASubmission(String subject, String submitter, LocalDateTime submitTime) {
    POASubmission.Builder builder = POASubmission.builder();
    builder.setSubject(subject);
    builder.setSubmitter(submitter);
    builder.setSubmitTime(submitTime);
    return builder.create();
  }
}
