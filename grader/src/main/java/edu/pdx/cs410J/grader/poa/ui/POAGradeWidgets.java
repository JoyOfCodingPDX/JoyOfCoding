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
  private final JButton recordGrade;

  public POAGradeWidgets() {
    isLateCheckbox = new JCheckBox("Late");
    totalPointsLabel = new JLabel("out of");
    score = new JTextField(4);
    recordGrade = new JButton("Save Grade");
  }

  public JCheckBox getIsLateCheckbox() {
    return isLateCheckbox;
  }

  @Override
  public void setIsEnabled(boolean isEnabled) {
    this.isLateCheckbox.setEnabled(isEnabled);
    this.score.setEnabled(isEnabled);
    this.recordGrade.setEnabled(isEnabled);
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
      this.recordGrade.setEnabled(false);

    } else {
      this.score.setBorder(null);
      this.recordGrade.setEnabled(true);
    }
  }

  @Override
  public void setScore(String score) {
    this.score.setText(score);
    this.recordGrade.setEnabled(true);
  }

  @Override
  public void setScoreHasBeenRecorded(boolean hasScoreBeenRecorded) {
    if (hasScoreBeenRecorded) {
      this.recordGrade.setText("Update Grade");

    } else {
      this.recordGrade.setText("Save Grade");
    }
  }

  @Override
  public void addRecordGradeHandler(RecordGradeHandler handler) {
    this.recordGrade.addActionListener(e -> handler.recordGrade());
  }

  public JComponent getGradeWidget() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
    panel.add(new JLabel("Grade:"));
    panel.add(Box.createHorizontalStrut(3));
    panel.add(this.score);
    panel.add(Box.createHorizontalStrut(3));
    panel.add(this.totalPointsLabel);
    panel.add(Box.createHorizontalStrut(6));
    panel.add(this.recordGrade);
    panel.add(Box.createHorizontalGlue());
    return panel;
  }
}
