package edu.pdx.cs410J.grader.poa.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.swing.*;
import java.awt.*;

@Singleton
public class POASubmissionInformationPanel extends JPanel {

  @Inject
  public POASubmissionInformationPanel(POASubmissionInformationWidgets submissionWidgets,
                                       GradeBookWidget gradeBookWidget,
                                       StudentsWidget studentsWidget,
                                       POAAssignmentsWidget assignmentsWidget,
                                       POAGradeWidgets gradeWidgets) {
    this.setLayout(new BorderLayout());

    JPanel information = new JPanel(new GridBagLayout());

    addComponentOnGrid(submissionWidgets.getSubmitterWidget(), information, 0, 0);
    addComponentOnGrid(studentsWidget, information, 0, 1);

    addComponentOnGrid(submissionWidgets.getSubjectWidget(), information, 1, 0);
    addComponentOnGrid(assignmentsWidget, information, 1, 1);

    addComponentOnGrid(submissionWidgets.getSubmissionTimeWidget(), information, 2, 0);

    addComponentOnGrid(gradeBookWidget, information, 3, 0);
    addComponentOnGrid(gradeWidgets.getGradeWidget(), information, 3, 1);

    this.add(information, BorderLayout.NORTH);
    this.add(submissionWidgets.getSubmissionContentWidget(), BorderLayout.CENTER);
  }

  private void addComponentOnGrid(JComponent component, JPanel container, int row, int column) {
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.anchor = GridBagConstraints.LINE_START;
    constraints.gridx = column;
    constraints.gridy = row;

    container.add(component, constraints);
  }

}
