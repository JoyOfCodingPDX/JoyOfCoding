package edu.pdx.cs410J.grader.poa.ui;

import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.poa.POASubmissionsView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.List;
import java.util.Vector;

@Singleton
public class POASubmissionsPanel extends JPanel implements POASubmissionsView {

  private final JList<String> submissions;
  private final JButton downloadSubmissions;

  public POASubmissionsPanel() {
    submissions = new JList<>();
    submissions.setVisibleRowCount(10);

    this.setLayout(new BorderLayout());

    this.add(new JScrollPane(submissions), BorderLayout.CENTER);

    downloadSubmissions = new JButton("Download");
    this.add(downloadSubmissions, BorderLayout.SOUTH);
  }

  @Override
  public void setPOASubmissionsDescriptions(List<String> strings) {
    submissions.setListData(new Vector<>(strings));
  }

  @Override
  public void addSubmissionSelectedListener(POASubmissionSelectedListener listener) {
    submissions.addListSelectionListener(e -> {
      if (isFinalEventInUserSelection(e)) {
        int selectedIndex = submissions.getSelectedIndex();
        if (selectedIndex >= 0) {
          listener.submissionSelected(selectedIndex);
        }
      }
    });
  }

  @Override
  public void addDownloadSubmissionsListener(DownloadSubmissionsListener listener) {
    this.downloadSubmissions.addActionListener(e -> listener.downloadSubmissions());
  }

  @Override
  public void selectPOASubmission(int index) {
    this.submissions.setSelectedIndex(index);
  }

  private boolean isFinalEventInUserSelection(ListSelectionEvent e) {
    return !e.getValueIsAdjusting();
  }
}
