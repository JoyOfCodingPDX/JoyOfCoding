package edu.pdx.cs410J.grader.scoring.ui;

import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.scoring.TestCaseOutputView;

import javax.swing.*;
import java.awt.*;

@Singleton
public class TestCaseOutputPanel extends ScorePanel implements TestCaseOutputView {

  private final JLabel description;
  private final JLabel command;
  private final JTextArea output;
  private final JTextField pointsDeducted;
  private final JTextField graderComment;

  public TestCaseOutputPanel() {
    description = new JLabel();
    description.setHorizontalAlignment(SwingConstants.CENTER);

    command = new JLabel();

    output = new JTextArea();
    output.setEditable(false);

    pointsDeducted = new JTextField(3);
    graderComment = new JTextField(20);


    this.setLayout(new BorderLayout());

    JPanel north = new JPanel();
    north.setLayout(new BoxLayout(north, BoxLayout.PAGE_AXIS));
    north.add(description);
    north.add(Box.createVerticalStrut(5));
    north.add(command);
    this.add(north, BorderLayout.NORTH);

    this.add(new JScrollPane(output), BorderLayout.CENTER);

    JPanel feedback = new JPanel();
    feedback.setLayout(new BoxLayout(feedback, BoxLayout.LINE_AXIS));
    feedback.add(new JLabel("Points Deducted: "));
    feedback.add(pointsDeducted);
    feedback.add(new JLabel("Grader Comment: "));
    feedback.add(graderComment);

    this.add(feedback, BorderLayout.SOUTH);
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
    this.pointsDeducted.setText(pointsDeducted);
  }

  @Override
  public void addGraderCommentChangeListener(GraderCommentChangeListener listener) {
    registerListenerOnTextFieldChange(this.graderComment, listener::onGraderCommentChange);
  }

  @Override
  public void setGraderComment(String comment) {
    this.graderComment.setText(comment);
  }

  @Override
  public void addPointsDeductedChangeListener(PointsDeductedChangeListener listener) {
    registerListenerOnTextFieldChange(this.pointsDeducted, listener::onPointsDeductedChange);
  }

  @Override
  public void setPointsDeductedIsValid(boolean isValidPointsDeducted) {
    setBorderBasedOnValidity(this.pointsDeducted, isValidPointsDeducted);
  }
}
