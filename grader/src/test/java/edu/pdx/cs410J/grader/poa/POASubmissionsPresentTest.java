package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class POASubmissionsPresentTest {

  private EventBus bus;
  private POASubmissionsPresenter presenter;
  private POASubmissionsView view;

  @Before
  public void setUp() {
    bus = new EventBus();
    view = mock(POASubmissionsView.class);
    presenter = new POASubmissionsPresenter(bus, view);
  }

  @Test
  public void poaSubmissionDownloadedEventPopulatesPOASubmissionView() {
    String subject = "POA for Project 1";
    String submitter = "Test Student";
    LocalDateTime submitTime = LocalDateTime.now();

    POASubmission submission = createPOASubmission(subject, submitter, submitTime);
    bus.post(submission);

    verify(view).setPOASubmissionsDescriptions(Collections.singletonList(subject));
  }

  private POASubmission createPOASubmission(String subject, String submitter, LocalDateTime submitTime) {
    POASubmission.Builder builder = POASubmission.builder();
    builder.setSubject(subject);
    builder.setSubmitter(submitter);
    builder.setSubmitTime(submitTime);
    return builder.create();
  }
}
