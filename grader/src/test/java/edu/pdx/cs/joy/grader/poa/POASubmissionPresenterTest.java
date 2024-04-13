package edu.pdx.cs.joy.grader.poa;

import com.google.common.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

public class POASubmissionPresenterTest extends POASubmissionTestCase {

  private POASubmissionView view;

  @BeforeEach
  @Override
  public void setUp() {
    super.setUp();

    this.view = mock(POASubmissionView.class);
    new POASubmissionPresenter(this.bus, this.view);
  }

  @Test
  public void submissionInformationDisplayedOnSubmissionSelectionEvent() throws POASubmissionView.CouldNotParseContent {
    String subject = "Subject";
    String submitter = "Submitter";
    LocalDateTime submitTime = LocalDateTime.now();
    String content = "Test Content";
    String contentType = "text/plain";
    POASubmission submission = createPOASubmission(subject, submitter, submitTime, content, contentType);

    this.bus.post(new POASubmissionSelected(submission));

    verify(view).setSubmissionSubject(subject);
    verify(view).setSubmissionSubmitter(submitter);
    verify(view).setSubmissionTime(POASubmissionPresenter.formatSubmissionTime(submitTime));
    verify(view).setContent(content, POASubmissionView.POAContentType.TEXT);
  }

  @Test
  public void whenContentContentThrowsExceptionAnUnhandledExceptionEventIsPosted() throws POASubmissionView.CouldNotParseContent {
    this.unhandledExceptionHandler = this::doNotFailTestWhenUnhandledExceptionEncountered;

    IOException exception = new IOException();
    POASubmissionView.CouldNotParseContent couldNotParse = new POASubmissionView.CouldNotParseContent(exception);
    doThrow(couldNotParse).when(this.view).setContent(anyString(), eq(POASubmissionView.POAContentType.HTML));

    UnhandledExceptionEventHandler handler = mock(UnhandledExceptionEventHandler.class);
    this.bus.register(handler);

    String subject = "Subject";
    String submitter = "Submitter";
    LocalDateTime submitTime = LocalDateTime.now();
    String content = "Test Content";
    String contentType = "text/html";
    POASubmission submission = createPOASubmission(subject, submitter, submitTime, content, contentType);

    this.bus.post(new POASubmissionSelected(submission));

    verify(view).setContent(content, POASubmissionView.POAContentType.HTML);

    ArgumentCaptor<UnhandledExceptionEvent> captor = ArgumentCaptor.forClass(UnhandledExceptionEvent.class);
    verify(handler).handleUnhandledExceptionEvent(captor.capture());

    assertThat(captor.getValue().getUnhandledException(), equalTo(couldNotParse));
    assertThat(captor.getValue().getUnhandledException().getCause(), equalTo(exception));
  }

  interface UnhandledExceptionEventHandler {
    @Subscribe
    void handleUnhandledExceptionEvent(UnhandledExceptionEvent event);
  }

}
