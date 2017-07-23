package edu.pdx.cs410J.grader.scoring.ui;

import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.scoring.ProjectSubmissionScoreView;

import javax.swing.*;

@Singleton
public class ProjectSubmissionScorePanel extends ScorePanel implements ProjectSubmissionScoreView {
  private final JLabel totalPoints;
  private final JTextField score;

  public ProjectSubmissionScorePanel() {
    totalPoints = new JLabel();
    score = new JTextField(3);

    this.add(new JLabel("Total score for submission:"));
    this.add(score);
    this.add(new JLabel("out of"));
    this.add(totalPoints);
  }

  @Override
  public void setTotalPoints(String totalPoints) {
    this.totalPoints.setText(totalPoints);
  }

  @Override
  public void setScore(String score) {
    this.score.setText(score);
  }

  @Override
  public void addScoreChangedListener(ScoreChangedListener listener) {
    registerListenerOnTextFieldChange(this.score, listener::scoreChanged);
  }

  @Override
  public void setScoreIsValid(boolean scoreIsValid) {
    setBorderBasedOnValidity(this.score, scoreIsValid);
  }

}
