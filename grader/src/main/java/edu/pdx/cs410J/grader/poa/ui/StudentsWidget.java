package edu.pdx.cs410J.grader.poa.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.poa.StudentsView;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Singleton
public class StudentsWidget extends JPanel implements StudentsView {

  private final JComboBox<String> students;

  @Inject
  public StudentsWidget() {
    students = new JComboBox<>();
    students.setEditable(false);

    this.setLayout(new BorderLayout());
    this.add(new JLabel("Student: "), BorderLayout.WEST);
    this.add(students, BorderLayout.CENTER);
  }

  @Override
  public void setStudents(List<String> students) {
    String[] array = students.toArray(new String[students.size()]);
    this.students.setModel(new DefaultComboBoxModel<>(array));
  }

  @Override
  public void setSelectedStudentIndex(int index) {
    this.students.setSelectedIndex(index);
  }

  @Override
  public void addSelectStudentHandler(SelectStudentHandler handler) {
    this.students.addActionListener(e -> {
      int index = students.getSelectedIndex();
      handler.studentSelected(index);
    });
  }
}
