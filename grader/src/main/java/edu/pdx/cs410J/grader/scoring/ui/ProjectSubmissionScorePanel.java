package edu.pdx.cs410J.grader.scoring.ui;

import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.scoring.ProjectSubmissionScoreView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

@Singleton
public class ProjectSubmissionScorePanel extends JPanel implements ProjectSubmissionScoreView {
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
    this.score.addActionListener(e -> listener.scoreChanged(score.getText()));
    this.score.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        listener.scoreChanged(score.getText());
      }
    });
  }

  @Override
  public void setScoreIsValid(boolean scoreIsValid) {
    if (scoreIsValid) {
      this.score.setBorder(BorderFactory.createEmptyBorder());

    } else {
      this.score.setBorder(BorderFactory.createLineBorder(Color.red, 2));
    }
  }
}
