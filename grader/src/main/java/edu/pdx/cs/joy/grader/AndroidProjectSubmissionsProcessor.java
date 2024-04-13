package edu.pdx.cs.joy.grader;

import edu.pdx.cs.joy.grader.gradebook.GradeBook;

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
    return "Project6";
  }
}
