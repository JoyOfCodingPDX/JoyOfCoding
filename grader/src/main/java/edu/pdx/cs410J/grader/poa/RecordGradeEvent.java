package edu.pdx.cs410J.grader.poa;

import edu.pdx.cs410J.grader.Assignment;
import edu.pdx.cs410J.grader.Student;

public class RecordGradeEvent {
  private final double score;
  private final Student student;
  private final Assignment assignment;
  private final boolean late;

  public RecordGradeEvent(double score, Student student, Assignment assignment, boolean late) {
    this.score = score;
    this.student = student;
    this.assignment = assignment;
    this.late = late;
  }

  public double getScore() {
    return score;
  }

  public Student getStudent() {
    return student;
  }

  public Assignment getAssignment() {
    return assignment;
  }

  public boolean isLate() {
    return late;
  }

  @Override
  public String toString() {
    return "Record " + (isLate() ? "late" : "on-time") + " grade of " + getScore() + " for " +
      getStudent().getFullName() + " on " + getAssignment().getName();
  }
}
