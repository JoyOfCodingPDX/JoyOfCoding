package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.Subscribe;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.swing.text.BadLocationException;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

public class POASubmissionPresenterTest extends POASubmissionTestCase {

  private POASubmissionView view;

  @Before
  @Override
  public void setUp() {
    super.setUp();

    this.view = mock(POASubmissionView.class);
    new POASubmissionPresenter(this.bus, this.view);
  }

  @Test
  public void submissionInformationDisplayedOnSubmissionSelectionEvent() throws IOException, BadLocationException {
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
  public void whenContentContentThrowsExceptionAnUnhandledExceptionEventIsPosted() throws IOException, BadLocationException {
    this.unhandledExceptionHandler = this::doNotFailTestWhenUnhandledExceptionEncountered;

    IOException exception = new IOException();
    doThrow(exception).when(this.view).setContent(anyString(), eq(POASubmissionView.POAContentType.HTML));

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

    assertThat(captor.getValue().getUnhandledException(), equalTo(exception));
  }

  static interface UnhandledExceptionEventHandler {
    @Subscribe
    public void handleUnhandledExceptionEvent(UnhandledExceptionEvent event);
  }

}
