package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class POASubmissionsPresentTest {

  private EventBus bus;
  private POASubmissionsView view;

  @Before
  public void setUp() {
    bus = new EventBus();
    view = mock(POASubmissionsView.class);
    new POASubmissionsPresenter(bus, view);
  }

  @Test
  public void poaSubmissionDownloadedEventPopulatesPOASubmissionView() {
    String subject = "POA for Project 1";
    String submitter = "Test Student";
    LocalDateTime submitTime = LocalDateTime.now();

    POASubmission submission = createPOASubmission(subject, submitter, submitTime);
    bus.post(submission);

    verify(view).setPOASubmissionsDescriptions(Arrays.asList(subject));
  }

  @Test
  public void submissionsDescriptionsAreDisplayedInOrderThatEventsWereSent() {
    String subject1 = "POA for Project 1";
    String subject2 = "POA for Project 2";
    String submitter = "Test Student";
    LocalDateTime submitTime = LocalDateTime.now();

    bus.post(createPOASubmission(subject1, submitter, submitTime));
    bus.post(createPOASubmission(subject2, submitter, submitTime));

    verify(view).setPOASubmissionsDescriptions(Arrays.asList(subject1));
    verify(view).setPOASubmissionsDescriptions(Arrays.asList(subject1, subject2));

  }

  private POASubmission createPOASubmission(String subject, String submitter, LocalDateTime submitTime) {
    POASubmission.Builder builder = POASubmission.builder();
    builder.setSubject(subject);
    builder.setSubmitter(submitter);
    builder.setSubmitTime(submitTime);
    return builder.create();
  }
}
