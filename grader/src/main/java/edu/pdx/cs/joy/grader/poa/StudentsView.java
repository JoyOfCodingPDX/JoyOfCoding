package edu.pdx.cs.joy.grader.poa;

import java.util.List;

public interface StudentsView {
  void setStudents(List<String> students);

  void setSelectedStudentIndex(int index);

  void addSelectStudentHandler(SelectStudentHandler handler);

  public interface SelectStudentHandler {
    void studentSelected(int index);
  }
}
