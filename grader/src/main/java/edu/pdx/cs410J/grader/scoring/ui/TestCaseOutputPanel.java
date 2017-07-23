package edu.pdx.cs410J.grader.scoring.ui;

import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.scoring.TestCaseOutputView;

import javax.swing.*;

@Singleton
public class TestCaseOutputPanel extends JPanel implements TestCaseOutputView {

  private final JLabel description;
  private final JLabel command;
  private final JTextArea output;

  public TestCaseOutputPanel() {
    description = new JLabel();
    description.setHorizontalAlignment(SwingConstants.CENTER);

    command = new JLabel();

    output = new JTextArea();
    output.setEditable(false);

    this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

    this.add(description);
    this.add(Box.createVerticalStrut(5));
    this.add(command);
    this.add(new JScrollPane(output));
  }

  @Override
  public void setDescription(String description) {
    this.description.setText(description);
  }

  @Override
  public void setCommand(String command) {
    this.command.setText(command);
  }

  @Override
  public void setOutput(String output) {
    this.output.setText(output);
  }

  @Override
  public void setPointsDeducted(String pointsDeducted) {
    throw new UnsupportedOperationException("This method is not implemented yet");
  }

  @Override
  public void addGraderCommentChangeListener(GraderCommentChangeListener listener) {
    throw new UnsupportedOperationException("This method is not implemented yet");
  }

  @Override
  public void setGraderComment(String comment) {
    throw new UnsupportedOperationException("This method is not implemented yet");
  }

  @Override
  public void addPointsDeductedChangeListener(PointsDeductedChangeListener listener) {
    throw new UnsupportedOperationException("This method is not implemented yet");
  }

  @Override
  public void setPointsDeductedIsValid(boolean isValidPointsDeducted) {
    throw new UnsupportedOperationException("This method is not implemented yet");
  }
}
