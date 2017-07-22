package edu.pdx.cs410J.grader.scoring.ui;

import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.scoring.ProjectSubmissionsView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.List;
import java.util.Vector;

@Singleton
public class ProjectSubmissionsPanel extends JPanel implements ProjectSubmissionsView {

  private final JList<String> submissionNames;

  public ProjectSubmissionsPanel() {
    submissionNames = new JList<>();
    submissionNames.setVisibleRowCount(30);

    this.setLayout(new BorderLayout());

    this.add(new JScrollPane(submissionNames), BorderLayout.CENTER);
  }

  @Override
  public void setProjectSubmissionNames(List<String> submissionNames) {
    this.submissionNames.setListData(new Vector<>(submissionNames));
  }

  @Override
  public void addSubmissionNameSelectedListener(SubmissionNameSelectedListener listener) {
    this.submissionNames.addListSelectionListener(e -> {
      if (isFinalEventInUserSelection(e)) {
        int selectedIndex = submissionNames.getSelectedIndex();
        if (selectedIndex >= 0) {
          listener.submissionSelected(selectedIndex);
        }
      }
    });
  }

  private boolean isFinalEventInUserSelection(ListSelectionEvent e) {
    return !e.getValueIsAdjusting();
  }

}
