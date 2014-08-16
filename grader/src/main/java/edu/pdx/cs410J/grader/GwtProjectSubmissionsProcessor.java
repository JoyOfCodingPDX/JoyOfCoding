package edu.pdx.cs410J.grader;

import java.io.File;

public class GwtProjectSubmissionsProcessor extends ZipFileSubmissionsProcessor {
  public GwtProjectSubmissionsProcessor(File directory, GradeBook gradeBook) {
    super(directory, gradeBook);
  }

  @Override
  protected String getAssignmentName() {
    return "Project5";
  }

  @Override
  public String getEmailFolder() {
    return "gwt projects";
  }
}
