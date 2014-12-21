package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class POASubmissionsPresenter {
  private final EventBus bus;
  private final POASubmissionsView view;
  private final List<POASubmission> submissions = new ArrayList<>();

  public POASubmissionsPresenter(EventBus bus, POASubmissionsView view) {
    this.bus = bus;
    this.view = view;

    this.bus.register(this);
  }

  @Subscribe
  public void displaySubmission(POASubmission submission) {
    this.submissions.add(submission);
    view.setPOASubmissionsDescriptions(getSubmissionsDescriptions());
  }

  private List<String> getSubmissionsDescriptions() {
    return submissions.stream().map(POASubmission::getSubject).collect(Collectors.toList());
  }
}
