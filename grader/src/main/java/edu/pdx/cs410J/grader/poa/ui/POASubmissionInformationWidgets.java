package edu.pdx.cs410J.grader.poa.ui;

import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.poa.POASubmissionView;

import javax.swing.*;
import java.awt.*;

@Singleton
public class POASubmissionInformationWidgets implements POASubmissionView {

  private final JLabel subjectLabel;
  private final JLabel submitterLabel;
  private final JLabel submissionTimeLabel;
  private final JTextArea submissionContent;

  public POASubmissionInformationWidgets() {
    this.subjectLabel = new JLabel();
    this.submitterLabel = new JLabel();
    this.submissionTimeLabel = new JLabel();
    this.submissionContent = new JTextArea();
  }

  public JComponent getSubjectWidget() {
    return createLabeledWidget("Subject:", this.subjectLabel);
  }

  private JComponent createLabeledWidget(String label, JLabel widget) {
    JPanel panel = new JPanel(new FlowLayout());
    panel.add(new JLabel(label));
    panel.add(widget);
    return panel;
  }

  public JComponent getSubmitterWidget() {
    return createLabeledWidget("Submitted by:", this.submitterLabel);
  }

  public JComponent getSubmissionTimeWidget() {
    return createLabeledWidget("Submitted on:", this.submissionTimeLabel);
  }

  @Override
  public void setSubmissionSubject(String subject) {
    this.subjectLabel.setText(subject);
  }

  @Override
  public void setSubmissionSubmitter(String submitter) {
    this.submitterLabel.setText(submitter);
  }

  @Override
  public void setSubmissionTime(String time) {
    this.submissionTimeLabel.setText(time);
  }

  public JComponent getSubmissionContentWidget() {
    return new JScrollPane(this.submissionContent);
  }
}
