package edu.pdx.cs410J.grader.scoring;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import edu.pdx.cs410J.grader.mvp.PresenterOnEventBus;

import java.text.NumberFormat;

public class ProjectSubmissionScorePresenter extends PresenterOnEventBus {
  private final ProjectSubmissionScoreView view;
  private final NumberFormat format;
  private ProjectSubmission submission;

  public ProjectSubmissionScorePresenter(EventBus bus, ProjectSubmissionScoreView view) {
    super(bus);
    this.view = view;

    format = NumberFormat.getNumberInstance();
    format.setMinimumFractionDigits(1);

    this.view.addScoreChangedListener(this::setSubmissionScore);
  }

  private void setSubmissionScore(String score) {
    Double value;
    if ("".equals(score)) {
      value = null;

    } else {
      try {
        value = Double.parseDouble(score);

      } catch (NumberFormatException ex) {
        this.view.setScoreIsValid(false);
        return;
      }
    }

    this.submission.setScore(value);
    this.view.setScoreIsValid(true);
  }

  @Subscribe
  public void populateViewWhenSubmissionIsSelected(ProjectSubmissionSelected selected) {
    submission = selected.getProjectSubmission();
    Double score = submission.getScore();
    if (score == null) {
      this.view.setScore("");

    } else {
      this.view.setScore(format.format(score));
    }

    this.view.setTotalPoints(format.format(submission.getTotalPoints()));
  }
}
