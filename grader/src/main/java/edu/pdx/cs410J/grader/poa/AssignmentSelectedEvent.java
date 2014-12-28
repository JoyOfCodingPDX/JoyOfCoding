package edu.pdx.cs410J.grader.poa;

import edu.pdx.cs410J.grader.Assignment;

public class AssignmentSelectedEvent {
  private final Assignment assignment;

  public AssignmentSelectedEvent(Assignment assignment) {
    this.assignment = assignment;
  }

  public Assignment getAssignment() {
    return assignment;
  }

  @Override
  public String toString() {
    return "Selected assignment " + assignment.getName();
  }
}
