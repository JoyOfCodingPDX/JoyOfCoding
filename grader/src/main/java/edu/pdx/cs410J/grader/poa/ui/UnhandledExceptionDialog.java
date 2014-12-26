package edu.pdx.cs410J.grader.poa.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.poa.UnhandledExceptionView;

import javax.swing.*;
import java.awt.*;

@Singleton
public class UnhandledExceptionDialog extends JDialog implements UnhandledExceptionView {

  private final JLabel message;
  private final JTextArea details;

  @Inject
  public UnhandledExceptionDialog(TopLevelJFrame parent) {
    super(parent, "Unhandled Exception Encountered", true);
    this.setVisible(false);

    this.message = new JLabel();
    this.details = new JTextArea(10, 50);
    this.details.setEditable(false);

    Container content = this.getContentPane();
    content.setLayout(new BorderLayout());

    content.add(this.message, BorderLayout.NORTH);
    content.add(new JScrollPane(this.details), BorderLayout.CENTER);

  }

  @Override
  public void setExceptionMessage(String message) {
    this.message.setText(message);
  }

  @Override
  public void setExceptionDetails(String details) {
    this.details.setText(details);
  }

  @Override
  public void displayView() {
    this.pack();
    this.setVisible(true);
  }

}
