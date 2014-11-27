package edu.pdx.cs410J.grader;

import javax.mail.MessagingException;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class StudentEmailAttachmentProcessor implements EmailAttachmentProcessor {
  protected final Logger logger = Logger.getLogger(this.getClass().getPackage().getName());
  protected final File directory;
  protected final GradeBook gradeBook;

  public StudentEmailAttachmentProcessor(File directory, GradeBook gradeBook) {
    this.directory = directory;
    this.gradeBook = gradeBook;
  }

  protected void logException(String message, Exception ex) {
    this.logger.log(Level.SEVERE, message, ex);
  }

  public abstract String getEmailFolder();

  protected void warn(String message) {
    this.logger.warning(message);
  }

  protected void info(String message) {
    this.logger.info(message);
  }

  protected void debug(String message) {
    this.logger.fine(message);
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
