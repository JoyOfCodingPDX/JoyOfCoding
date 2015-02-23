package edu.pdx.cs410J.grader.poa;

import java.io.File;

public class LoadGradeBook {
  private final File file;

  public LoadGradeBook(File file) {
    this.file = file;
  }

  public File getFile() {
    return file;
  }

  @Override
  public String toString() {
    return "Load grade book from " + this.file;
  }
}
