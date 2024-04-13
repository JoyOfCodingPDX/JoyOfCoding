package edu.pdx.cs.joy.grader.poa;

import edu.pdx.cs.joy.grader.gradebook.GradeBook;

public class GradeBookLoaded {
  private final GradeBook gradeBook;

  public GradeBookLoaded(GradeBook book) {
    this.gradeBook = book;
  }

  public GradeBook getGradeBook() {
    return gradeBook;
  }

  @Override
  public String toString() {
    return "Loaded grade book \"" + gradeBook.getClassName() + "\"";
  }
}
