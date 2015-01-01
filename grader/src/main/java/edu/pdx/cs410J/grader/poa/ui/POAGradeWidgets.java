package edu.pdx.cs410J.grader.poa.ui;

import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.poa.POAGradeView;

import javax.swing.*;

@Singleton
public class POAGradeWidgets implements POAGradeView {
  private final JCheckBox isLateCheckbox;

  public POAGradeWidgets() {
    isLateCheckbox = new JCheckBox("Late");
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
    this.isLateCheckbox.addItemListener(e -> {
      handler.setIsLate(isLateCheckbox.isSelected());
    });
  }
}
