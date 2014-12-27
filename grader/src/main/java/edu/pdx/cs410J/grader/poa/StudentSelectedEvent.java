package edu.pdx.cs410J.grader.poa;

import edu.pdx.cs410J.grader.Student;

public class StudentSelectedEvent {
  private Student selectedStudent;

  public StudentSelectedEvent(Student selectedStudent) {
    this.selectedStudent = selectedStudent;
  }

  public Student getSelectedStudent() {
    return selectedStudent;
  }

  @Override
  public String toString() {
    return "Selected student " + selectedStudent;
  }
}
