package edu.pdx.cs410J.grader;

import java.io.File;
import java.io.IOException;

public abstract class StudentEmailAttachmentProcessor implements EmailAttachmentProcessor {
  protected final File directory;
  protected final GradeBook gradeBook;

  public StudentEmailAttachmentProcessor(File directory, GradeBook gradeBook) {
    this.directory = directory;
    this.gradeBook = gradeBook;
  }

  protected void logException(String message, IOException ex) {
    System.err.println(message);
    ex.printStackTrace(System.err);
  }

  public abstract String getEmailFolder();
}
