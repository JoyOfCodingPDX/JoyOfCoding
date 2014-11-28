package edu.pdx.cs410J.grader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.io.File;

public abstract class StudentEmailAttachmentProcessor implements EmailAttachmentProcessor {
  protected final Logger logger = LoggerFactory.getLogger(this.getClass().getPackage().getName());
  protected final File directory;
  protected final GradeBook gradeBook;

  public StudentEmailAttachmentProcessor(File directory, GradeBook gradeBook) {
    this.directory = directory;
    this.gradeBook = gradeBook;
  }

  protected void logException(String message, Exception ex) {
    this.logger.error(message, ex);
  }

  public abstract String getEmailFolder();

  protected void warn(String message) {
    this.logger.warn(message);
  }

  protected void info(String message) {
    this.logger.info(message);
  }

  protected void debug(String message) {
    this.logger.debug(message);
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
