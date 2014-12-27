package edu.pdx.cs410J.grader.poa;

import java.util.List;

public interface POAAssignmentsView {
  void setAssignments(List<String> assignments);

  void setSelectedAssignment(int index);
}
