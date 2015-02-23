package edu.pdx.cs410J.grader.poa;

import edu.pdx.cs410J.grader.GradeBook;

public class SaveGradeBook {
  private final GradeBook gradeBook;

  public SaveGradeBook(GradeBook gradeBook) {
    this.gradeBook = gradeBook;
  }

  public GradeBook getGradeBook() {
    return gradeBook;
  }

  @Override
  public String toString() {
    return "Save grade book " + this.gradeBook.getClassName();
  }
}
