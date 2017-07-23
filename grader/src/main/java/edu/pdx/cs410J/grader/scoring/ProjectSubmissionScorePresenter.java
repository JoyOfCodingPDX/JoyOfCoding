package edu.pdx.cs410J.grader.scoring;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ProjectSubmissionScorePresenter extends ScorePresenter {
  private final ProjectSubmissionScoreView view;
  private ProjectSubmission submission;

  @Inject
  public ProjectSubmissionScorePresenter(EventBus bus, ProjectSubmissionScoreView view) {
    super(bus);
    this.view = view;

    this.view.addScoreChangedListener(this::setSubmissionScore);
  }

  private void setSubmissionScore(String score) {
    try {
      Double value = getValidScoreValue(score);
      this.submission.setScore(value);
      this.view.setScoreIsValid(true);

    } catch (InvalidScoreValue invalidScoreValue) {
      this.view.setScoreIsValid(false);
    }
  }

  private Double getValidScoreValue(String score) throws InvalidScoreValue {
    if ("".equals(score)) {
      return null;

    } else {
      try {
        Double value = Double.parseDouble(score);
        if (value >= 0.0 && value <= this.submission.getTotalPoints()) {
          return value;

        } else {
          throw new InvalidScoreValue(score);
        }

      } catch (NumberFormatException ex) {
        throw new InvalidScoreValue(score);
      }
    }
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
    this.view.setScoreIsValid(true);

    this.view.setTotalPoints(format.format(submission.getTotalPoints()));
  }

  private class InvalidScoreValue extends Exception {
    public InvalidScoreValue(String score) {
      super(score);
    }
  }
}
