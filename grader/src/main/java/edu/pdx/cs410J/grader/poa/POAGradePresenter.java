package edu.pdx.cs410J.grader.poa;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.Assignment;
import edu.pdx.cs410J.grader.Student;

@Singleton
public class POAGradePresenter {
  private final EventBus bus;
  private final POAGradeView view;
  private POASubmission submission;
  private Assignment assignment;
  private Student student;
  private boolean isLate;
  private Double score;

  @Inject
  public POAGradePresenter(EventBus bus, POAGradeView view) {
    this.bus = bus;
    this.view = view;

    this.bus.register(this);

    this.view.addIsLateHandler(POAGradePresenter.this::setIsLate);
    this.view.addScoreValueHandler(new POAGradeView.ScoreValueHandler() {
      @Override
      public void scoreValue(String value) {
        setScoreValue(value);
      }
    });
  }

  @Subscribe
  public void determineIfSubmissionIsLate(POASubmissionSelected event) {
    this.submission = event.getSubmission();

    clearScore();
    determineIfPOAIsLate();
  }

  private void clearScore() {
    this.score = null;
    this.view.setErrorInScore(false);
    this.view.setScore("");
  }

  @Subscribe
  public void determineIfStudentsPOAIsLate(StudentSelectedEvent event) {
    this.student = event.getSelectedStudent();

    determineIfPOAIsLate();
  }

  @Subscribe
  public void determineIfAssignmentIsLate(AssignmentSelectedEvent event) {
    this.assignment = event.getAssignment();

    determineIfPOAIsLate();

    this.view.setTotalPoints(formatTotalPoints(this.assignment.getPoints()));
  }

  private void determineIfPOAIsLate() {
    if (this.submission != null && this.assignment != null && this.student != null) {
      enableView();

      if (this.assignment.isSubmissionLate(this.submission.getSubmitTime())) {
        setIsLate(true);

      } else {
        setIsLate(false);
      }

    } else {
      disableView();
    }
  }

  private void setIsLate(boolean isLate) {
    this.isLate = isLate;
    this.view.setIsLate(isLate);
  }

  private void enableView() {
    this.view.setIsEnabled(true);
  }

  private void disableView() {
    setIsLate(false);
    this.view.setIsEnabled(false);
  }

  @VisibleForTesting
  boolean isLate() {
    return this.isLate;
  }

  @VisibleForTesting
  static String formatTotalPoints(double points) {
    return String.format("%.2f", points);
  }

  public void setScoreValue(String scoreValue) {
    try {
      double score = new Double(scoreValue);

      if (score < 0.0) {
        this.score = null;
        this.view.setErrorInScore(true);

      } else {
        this.score = score;
        this.view.setErrorInScore(false);
      }

    } catch (NumberFormatException ex) {
      this.score = null;
      this.view.setErrorInScore(true);
    }
  }

  public Double getScore() {
    return score;
  }
}
