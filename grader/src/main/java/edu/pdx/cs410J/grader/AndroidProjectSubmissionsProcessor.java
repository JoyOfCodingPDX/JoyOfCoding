package edu.pdx.cs410J.grader;

import java.io.File;

public class AndroidProjectSubmissionsProcessor extends ZipFileSubmissionsProcessor {
  public AndroidProjectSubmissionsProcessor(File directory, GradeBook gradeBook) {
    super(directory, gradeBook);
  }

  @Override
  public String getEmailFolder() {
    return "android projects";
  }

  @Override
  protected String getAssignmentName() {
    return "Project5";
  }
}
