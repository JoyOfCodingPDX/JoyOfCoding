package edu.pdx.cs410J.grader.poa.ui;

import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.poa.POAGradeView;

import javax.swing.*;
import java.awt.*;

@Singleton
public class POAGradeWidgets implements POAGradeView {
  private final JCheckBox isLateCheckbox;
  private final JLabel totalPointsLabel;

  public POAGradeWidgets() {
    isLateCheckbox = new JCheckBox("Late");
    totalPointsLabel = new JLabel("out of");
  }

  public JCheckBox getIsLateCheckbox() {
    return isLateCheckbox;
  }

  @Override
  public void setIsEnabled(boolean isEnabled) {
    this.isLateCheckbox.setEnabled(isEnabled);
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
    throw new UnsupportedOperationException("This method is not implemented yet");
  }

  @Override
  public void setErrorInScore(boolean errorInScore) {
    throw new UnsupportedOperationException("This method is not implemented yet");
  }

  public JComponent getGradeWidget() {
    JPanel panel = new JPanel(new FlowLayout());
    panel.add(this.totalPointsLabel);
    return panel;
  }
}
