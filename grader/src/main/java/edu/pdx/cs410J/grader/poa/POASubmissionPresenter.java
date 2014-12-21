package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class POASubmissionPresenter {
  private final POASubmissionView view;

  public POASubmissionPresenter(EventBus bus, POASubmissionView view) {
    this.view = view;

    bus.register(this);
  }

  @Subscribe
  public void displayPOASubmissionInformation(POASubmissionSelected event) {
    POASubmission submission = event.getSubmission();

    this.view.setSubmissionSubject(submission.getSubject());
    this.view.setSubmissionSubmitter(submission.getSubmitter());
    this.view.setSubmissionTime(formatSubmissionTime(submission.getSubmitTime()));
  }

  static String formatSubmissionTime(LocalDateTime submitTime) {
    return submitTime.format(DateTimeFormatter.ofPattern("M/d/yyyy h:mm a"));
  }
}
