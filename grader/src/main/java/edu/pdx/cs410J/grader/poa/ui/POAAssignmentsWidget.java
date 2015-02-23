package edu.pdx.cs410J.grader.poa.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.poa.POAAssignmentsView;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Singleton
public class POAAssignmentsWidget extends JPanel implements POAAssignmentsView {
  private final JComboBox<String> assignments;
  private final JLabel dueDate;

  @Inject
  public POAAssignmentsWidget() {
    assignments = new JComboBox<>();
    assignments.setEditable(false);

    this.dueDate = new JLabel("Due:");

    this.setLayout(new BorderLayout());
    this.add(new JLabel("POA:"), BorderLayout.WEST);
    this.add(this.assignments, BorderLayout.CENTER);
    this.add(this.dueDate, BorderLayout.EAST);
  }

  @Override
  public void setAssignments(List<String> assignments) {
    String[] array = assignments.toArray(new String[assignments.size()]);
    this.assignments.setModel(new DefaultComboBoxModel<>(array));
  }

  @Override
  public void setSelectedAssignment(int index) {
    this.assignments.setSelectedIndex(index);
  }

  @Override
  public void addAssignmentSelectedHandler(AssignmentSelectedHandler handler) {
    this.assignments.addActionListener(e -> {
      int index = assignments.getSelectedIndex();
      handler.assignmentSelected(index);
    });
  }

  @Override
  public void setSelectedAssignmentDueDate(String dueDate) {
    this.dueDate.setText("Due: " + dueDate);
  }
}
