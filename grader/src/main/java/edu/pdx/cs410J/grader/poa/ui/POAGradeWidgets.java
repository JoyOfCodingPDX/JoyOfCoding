package edu.pdx.cs410J.grader.poa.ui;

import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.poa.POAGradeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

@Singleton
public class POAGradeWidgets implements POAGradeView {
  private final JCheckBox isLateCheckbox;
  private final JLabel totalPointsLabel;
  private final JTextField score;

  public POAGradeWidgets() {
    isLateCheckbox = new JCheckBox("Late");
    totalPointsLabel = new JLabel("out of");
    score = new JTextField(4);
  }

  public JCheckBox getIsLateCheckbox() {
    return isLateCheckbox;
  }

  @Override
  public void setIsEnabled(boolean isEnabled) {
    this.isLateCheckbox.setEnabled(isEnabled);
    this.score.setEnabled(isEnabled);
  }

  @Override
  public void setIsLate(boolean isLate) {
    this.isLateCheckbox.setSelected(isLate);
  }

  @Override
  public void addIsLateHandler(IsLateHandler handler) {
    this.isLateCheckbox.addItemListener(e -> handler.setIsLate(isLateCheckbox.isSelected()));
  }

  @Override
  public void setTotalPoints(String totalPoints) {
    this.totalPointsLabel.setText("out of " + totalPoints);
  }

  @Override
  public void addScoreValueHandler(ScoreValueHandler handler) {
    this.score.addFocusListener(new FocusListener() {
      @Override
      public void focusGained(FocusEvent e) {

      }

      @Override
      public void focusLost(FocusEvent e) {
        handler.scoreValue(score.getText());
      }
    });
  }

  @Override
  public void setErrorInScore(boolean errorInScore) {
    if (errorInScore) {
      this.score.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

    } else {
      this.score.setBorder(null);
    }
  }

  @Override
  public void setScore(String score) {
    this.score.setText(score);
  }

  public JComponent getGradeWidget() {
    JPanel panel = new JPanel(new FlowLayout());
    panel.add(new JLabel("Grade:"));
    panel.add(this.score);
    panel.add(this.totalPointsLabel);
    return panel;
  }
}
