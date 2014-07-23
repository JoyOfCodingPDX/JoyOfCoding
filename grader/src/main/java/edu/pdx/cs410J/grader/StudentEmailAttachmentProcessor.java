package edu.pdx.cs410J.grader;

import javax.mail.MessagingException;
import java.io.File;

public abstract class StudentEmailAttachmentProcessor implements EmailAttachmentProcessor {
  protected final File directory;
  protected final GradeBook gradeBook;

  public StudentEmailAttachmentProcessor(File directory, GradeBook gradeBook) {
    this.directory = directory;
    this.gradeBook = gradeBook;
  }

  protected void logException(String message, Exception ex) {
    System.err.println(message);
    ex.printStackTrace(System.err);
  }

  public abstract String getEmailFolder();

  protected void warn(String message) {
    System.out.println(message);
  }

  protected class SubmissionException extends Exception {
    public SubmissionException(String message) {
      super(message);
    }

    public SubmissionException(String message, MessagingException cause) {
      super(message, cause);
    }
  }
}
