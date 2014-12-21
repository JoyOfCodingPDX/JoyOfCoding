package edu.pdx.cs410J.grader.poa.ui;

import edu.pdx.cs410J.grader.poa.POASubmissionsView;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class POASubmissionsPanel extends JPanel implements POASubmissionsView {

  private final JList<String> submissions;

  public POASubmissionsPanel() {
    submissions = new JList<>();
    submissions.setVisibleRowCount(10);

    this.setLayout(new BorderLayout());

    this.add(new JScrollPane(submissions), BorderLayout.CENTER);

    this.add(new JLabel("Hello??"), BorderLayout.SOUTH);
  }

  @Override
  public void setPOASubmissionsDescriptions(List<String> strings) {
    submissions.setListData(new Vector<>(strings));
  }
}
