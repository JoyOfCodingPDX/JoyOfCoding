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
    this.view.addScoreSavedListener(this::publishSavedScoreMessage);
  }

  private void publishSavedScoreMessage() {
    ProjectSubmissionScoreSaved saved = new ProjectSubmissionScoreSaved(this.submission);
    publishEvent(saved);
  }

  private void setSubmissionScore(String score) {
    try {
      Double value = getValidScoreValue(score);
      this.submission.setScore(value);
      this.view.setScoreIsValid(true);

    } catch (InvalidScoreValue ex) {
      this.view.setScoreIsValid(false);
    }
  }

  @Override
  protected boolean isScoreInValidRange(Double score) {
    return score >= 0.0 && score <= this.submission.getTotalPoints();
  }

  @Subscribe
  public void populateViewWhenSubmissionIsSelected(ProjectSubmissionSelected selected) {
    submission = selected.getProjectSubmission();
    Double score = submission.getScore();
    if (score == null) {
      score = submission.getTotalPoints();
    }
    this.view.setScore(format.format(score));
    this.view.setScoreIsValid(true);

    this.view.setTotalPoints(format.format(submission.getTotalPoints()));
  }

  @Subscribe
  public void updateScoreWhenTestCaseOutputIsUpdate(TestCaseOutputUpdated updated) {
    if (submission.getTestCaseOutputs().contains(updated.getTestCaseOutput())) {
      double score = submission.getTotalPoints();
      double totalPointsDeducted = submission.getTestCaseOutputs().stream().mapToDouble(this::getPointsDeducted).sum();
      score -= totalPointsDeducted;
      this.submission.setScore(score);
      this.view.setScoreIsValid(true);
      this.view.setScore(format.format(score));
    }
  }

  private double getPointsDeducted(TestCaseOutput testCaseOutput) {
    Double pointsDeducted = testCaseOutput.getPointsDeducted();
    if (pointsDeducted == null) {
      return 0.0;

    } else {
      return pointsDeducted.doubleValue();
    }
  }
}
