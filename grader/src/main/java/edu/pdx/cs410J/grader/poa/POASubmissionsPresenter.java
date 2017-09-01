package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import edu.pdx.cs410J.grader.mvp.PresenterOnEventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class POASubmissionsPresenter extends PresenterOnEventBus {
  private final POASubmissionsView view;
  private final List<POASubmission> submissions = new ArrayList<>();

  private int selectedSubmissionIndex = 0;

  @Inject
  public POASubmissionsPresenter(EventBus bus, POASubmissionsView view) {
    super(bus);
    this.view = view;

    this.view.addSubmissionSelectedListener(this::poaSubmissionSelected);
    this.view.addDownloadSubmissionsListener(this::fireDownloadSubmissionsEvent);
  }

  private void fireDownloadSubmissionsEvent() {
    publishEvent(new DownloadPOASubmissionsRequest());
  }

  private void poaSubmissionSelected(int index) {
    this.selectedSubmissionIndex = index;
    publishEvent(new POASubmissionSelected(this.submissions.get(index)));
  }

  @Subscribe
  public void displaySubmission(POASubmission submission) {
    this.submissions.add(submission);
    view.setPOASubmissionsDescriptions(getSubmissionsDescriptions());
  }

  private List<String> getSubmissionsDescriptions() {
    return submissions.stream().map(POASubmission::getSubject).collect(Collectors.toList());
  }

  @Subscribe
  public void selectNextPOA(SelectNextPOAEvent event) {
    int nextIndex = this.selectedSubmissionIndex + 1;

    if (nextIndex < this.submissions.size()) {
      view.selectPOASubmission(nextIndex);
      poaSubmissionSelected(nextIndex);
    }
  }
}
