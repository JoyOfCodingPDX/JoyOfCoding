package edu.pdx.cs.joy.grader.poa.ui;

import com.google.inject.Singleton;
import edu.pdx.cs.joy.grader.poa.StatusMessageView;

import javax.swing.*;

@Singleton
public class StatusMessageWidget extends JPanel implements StatusMessageView {

  private final JLabel label;

  public StatusMessageWidget() {
    this.label = new JLabel("");
    this.add(this.label);
  }

  @Override
  public void setStatusMessage(String message) {
    this.label.setText(message);
  }
}
