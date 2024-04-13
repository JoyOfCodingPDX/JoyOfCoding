package edu.pdx.cs.joy.grader.poa;

import edu.pdx.cs.joy.grader.gradebook.Student;

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
