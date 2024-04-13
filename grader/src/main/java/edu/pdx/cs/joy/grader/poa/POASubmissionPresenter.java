package edu.pdx.cs.joy.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Singleton
public class POASubmissionPresenter {
  private final POASubmissionView view;
  private final EventBus bus;

  @Inject
  public POASubmissionPresenter(EventBus bus, POASubmissionView view) {
    this.view = view;

    bus.register(this);
    this.bus = bus;
  }

  @Subscribe
  public void displayPOASubmissionInformation(POASubmissionSelected event) {
    POASubmission submission = event.getSubmission();

    this.view.setSubmissionSubject(submission.getSubject());
    this.view.setSubmissionSubmitter(submission.getSubmitter());
    this.view.setSubmissionTime(formatSubmissionTime(submission.getSubmitTime()));
    String contentType = submission.getContentType();
    boolean isHtml = contentType.toLowerCase().contains("text/html");

    try {
      this.view.setContent(submission.getContent(), isHtml ? POASubmissionView.POAContentType.HTML : POASubmissionView.POAContentType.TEXT);

    } catch (POASubmissionView.CouldNotParseContent ex) {
      this.bus.post(new UnhandledExceptionEvent(ex));
    }
  }

  static String formatSubmissionTime(LocalDateTime submitTime) {
    return submitTime.format(DateTimeFormatter.ofPattern("E M/d/yyyy h:mm a"));
  }
}
