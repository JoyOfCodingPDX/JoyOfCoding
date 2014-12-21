package edu.pdx.cs410J.grader.poa.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.swing.*;
import java.awt.*;

@Singleton
public class POASubmissionInformationPanel extends JPanel {

  @Inject
  public POASubmissionInformationPanel(POASubmissionInformationWidgets widgets) {
    this.setLayout(new BorderLayout());

    JPanel information = new JPanel(new GridBagLayout());

    addComponentOnGrid(widgets.getSubjectWidget(), information, 0, 0);
    addComponentOnGrid(widgets.getSubmitterWidget(), information, 1, 0);
    addComponentOnGrid(widgets.getSubmissionTimeWidget(), information, 0, 1);

    this.add(information, BorderLayout.NORTH);
    this.add(widgets.getSubmissionContentWidget(), BorderLayout.CENTER);
  }

  private void addComponentOnGrid(JComponent component, JPanel container, int row, int column) {
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.gridx = column;
    constraints.gridy = row;

    container.add(component, constraints);
  }

}
