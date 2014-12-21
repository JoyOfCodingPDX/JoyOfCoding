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
    POASubmission submission = createPOASubmission("Subject", "Submitter", LocalDateTime.now());

    this.bus.post(new POASubmissionSelected(submission));

    verify(view).setSubmissionSubject(submission.getSubject());
    verify(view).setSubmissionSubmitter(submission.getSubmitter());
    verify(view).setSubmissionTime(POASubmissionPresenter.formatSubmissionTime(submission.getSubmitTime()));
  }

}
