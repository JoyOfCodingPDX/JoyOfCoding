package edu.pdx.cs410J.grader;

import java.io.File;

public class KoansSubmissionsProcessor extends ZipFileSubmissionsProcessor {
  public KoansSubmissionsProcessor(File directory, GradeBook gradeBook) {
    super(directory, gradeBook);
  }

  @Override
  protected String getAssignmentName() {
    return "koans";
  }

  @Override
  public String getEmailFolder() {
    return "koans";
  }
}
