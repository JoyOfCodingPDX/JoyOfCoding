package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.Subscribe;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;

public class POASubmissionsPresenterTest extends POASubmissionTestCase {

  private POASubmissionsView view;

  @Override
  @Before
  public void setUp() {
    super.setUp();
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

  @Test
  public void selectingSubmissionFiresPOASubmissionSelectedEvent() {
    // Given that there are two POA submissions
    String subject1 = "POA for Project 1";
    String subject2 = "POA for Project 2";
    String submitter = "Test Student";
    LocalDateTime submitTime = LocalDateTime.now();

    POASubmissionSelectedHandler handler = mock(POASubmissionSelectedHandler.class);
    bus.register(handler);

    ArgumentCaptor<POASubmissionsView.POASubmissionSelectedListener> listener = ArgumentCaptor.forClass(POASubmissionsView.POASubmissionSelectedListener.class);
    verify(view).addSubmissionSelectedListener(listener.capture());

    bus.post(createPOASubmission(subject1, submitter, submitTime));
    POASubmission submission = createPOASubmission(subject2, submitter, submitTime);
    bus.post(submission);

    // When the user selects the second POA submission...
    listener.getValue().submissionSelected(1);

    // Then a POASubmissionSelected event for that submission is fired

    ArgumentCaptor<POASubmissionSelected> eventCaptor = ArgumentCaptor.forClass(POASubmissionSelected.class);
    verify(handler).handle(eventCaptor.capture());
    assertThat(eventCaptor.getValue().getSubmission(), equalTo(submission));

    verifyNoMoreInteractions(handler);
  }

  @Test
  public void displayNextPOAEventDisplaysNextPOA() {
    // Given that there are two POA submissions
    String subject1 = "POA for Project 1";
    String subject2 = "POA for Project 2";
    String submitter = "Test Student";
    LocalDateTime submitTime = LocalDateTime.now();

    POASubmissionSelectedHandler handler = mock(POASubmissionSelectedHandler.class);
    bus.register(handler);

    ArgumentCaptor<POASubmissionsView.POASubmissionSelectedListener> listener = ArgumentCaptor.forClass(POASubmissionsView.POASubmissionSelectedListener.class);
    verify(view).addSubmissionSelectedListener(listener.capture());

    POASubmission submission1 = createPOASubmission(subject1, submitter, submitTime);
    bus.post(submission1);
    POASubmission submission2 = createPOASubmission(subject2, submitter, submitTime);
    bus.post(submission2);

    // When a DisplayNextPOAEvent is posted
    bus.post(new SelectNextPOAEvent());

    // Then the View is updated
    verify(view).selectPOASubmission(1);

    // Then a POASubmissionSelected event for the second submission is fired

    ArgumentCaptor<POASubmissionSelected> eventCaptor = ArgumentCaptor.forClass(POASubmissionSelected.class);
    verify(handler).handle(eventCaptor.capture());
    assertThat(eventCaptor.getValue().getSubmission(), equalTo(submission2));

    verifyNoMoreInteractions(handler);
  }

  @Test
  public void displayingNextPOAAtEndOfSubmissionListDoesNothing() {
    // Given that there is only one POA Submission
    String subject1 = "POA for Project 1";
    String submitter = "Test Student";
    LocalDateTime submitTime = LocalDateTime.now();

    POASubmissionSelectedHandler handler = mock(POASubmissionSelectedHandler.class);
    bus.register(handler);

    ArgumentCaptor<POASubmissionsView.POASubmissionSelectedListener> listener = ArgumentCaptor.forClass(POASubmissionsView.POASubmissionSelectedListener.class);
    verify(view).addSubmissionSelectedListener(listener.capture());

    POASubmission submission1 = createPOASubmission(subject1, submitter, submitTime);
    bus.post(submission1);

    // When a DisplayNextPOAEvent is posted
    bus.post(new SelectNextPOAEvent());

    // Then nothing happens
    verify(view, times(0)).selectPOASubmission(anyInt());

    verifyNoMoreInteractions(handler);
  }

  private interface POASubmissionSelectedHandler {
    @Subscribe
    public void handle(POASubmissionSelected selected);
  }

  @Test
  public void downloadSubmissionsInViewFiresDownloadSubmissionsEvent() {
    ArgumentCaptor<POASubmissionsView.DownloadSubmissionsListener> listener = ArgumentCaptor.forClass(POASubmissionsView.DownloadSubmissionsListener.class);
    verify(this.view).addDownloadSubmissionsListener(listener.capture());

    DownloadSubmissionsHandler handler = mock(DownloadSubmissionsHandler.class);
    this.bus.register(handler);

    listener.getValue().downloadSubmissions();

    ArgumentCaptor<DownloadPOASubmissionsRequest> event = ArgumentCaptor.forClass(DownloadPOASubmissionsRequest.class);
    verify(handler).handleDownloadSubmissions(event.capture());

    assertThat(event.getValue(), notNullValue());
  }

  public interface DownloadSubmissionsHandler {
    @Subscribe
    public void handleDownloadSubmissions(DownloadPOASubmissionsRequest event);
  }

}
