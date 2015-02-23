package edu.pdx.cs410J.grader.poa;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
  public void submissionInformationDisplayedOnSubmissionSelectionEvent() {
    String subject = "Subject";
    String submitter = "Submitter";
    LocalDateTime submitTime = LocalDateTime.now();
    String content = "Test Content";
    POASubmission submission = createPOASubmission(subject, submitter, submitTime, content);

    this.bus.post(new POASubmissionSelected(submission));

    verify(view).setSubmissionSubject(subject);
    verify(view).setSubmissionSubmitter(submitter);
    verify(view).setSubmissionTime(POASubmissionPresenter.formatSubmissionTime(submitTime));
    verify(view).setContent(content);
  }

}
