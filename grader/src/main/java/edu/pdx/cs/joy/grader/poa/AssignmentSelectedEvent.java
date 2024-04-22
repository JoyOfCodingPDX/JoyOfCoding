package edu.pdx.cs.joy.grader.poa;

import edu.pdx.cs.joy.grader.gradebook.Assignment;

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
