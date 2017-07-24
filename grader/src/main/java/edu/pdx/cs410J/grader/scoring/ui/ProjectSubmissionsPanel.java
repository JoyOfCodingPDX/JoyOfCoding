package edu.pdx.cs410J.grader.scoring.ui;

import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.scoring.ProjectSubmissionsView;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;

@Singleton
public class ProjectSubmissionsPanel extends JPanelWithJList implements ProjectSubmissionsView {

  private final JList<String> ungradedSubmissionNames;
  private final JList<String> gradedSubmissionNames;

  public ProjectSubmissionsPanel() {
    ungradedSubmissionNames = new JList<>();
    ungradedSubmissionNames.setVisibleRowCount(15);

    gradedSubmissionNames = new JList<>();
    gradedSubmissionNames.setVisibleRowCount(15);

    this.setLayout(new BorderLayout());

    JScrollPane ungradedScroll = new JScrollPane(ungradedSubmissionNames);
    ungradedScroll.setBorder(BorderFactory.createTitledBorder("Ungraded Submissions"));
    this.add(ungradedScroll, BorderLayout.NORTH);

    JScrollPane gradedScroll = new JScrollPane(gradedSubmissionNames);
    gradedScroll.setBorder(BorderFactory.createTitledBorder("Graded Submissions"));
    this.add(gradedScroll, BorderLayout.SOUTH);
  }

  @Override
  public void setUngradedProjectSubmissionNames(List<String> submissionNames) {
    this.ungradedSubmissionNames.setListData(new Vector<>(submissionNames));
  }

  @Override
  public void addUngradedSubmissionNameSelectedListener(SubmissionNameSelectedListener listener) {
    registerListenerOnListItemSelection(this.ungradedSubmissionNames, listener::submissionNameSelected);
  }

  @Override
  public void setSelectedUngradedSubmission(int index) {
    this.ungradedSubmissionNames.setSelectedIndex(index);
  }

  @Override
  public void setGradedProjectSubmissionNames(List<String> submissionNames) {
    this.gradedSubmissionNames.setListData(new Vector<>(submissionNames));
  }

  @Override
  public void addGradedSubmissionNameSelectedListener(SubmissionNameSelectedListener listener) {
    registerListenerOnListItemSelection(this.gradedSubmissionNames, listener::submissionNameSelected);
  }

}
