package edu.pdx.cs410J.grader;

import java.io.File;
import java.io.InputStream;

public class KoansSubmissionsProcessor extends StudentEmailAttachmentProcessor{
  public KoansSubmissionsProcessor(File directory, GradeBook gradeBook) {
    super(directory, gradeBook);
  }

  @Override
  public void processAttachment(String fileName, InputStream inputStream) {
    throw new UnsupportedOperationException("This method is not implemented yet");
  }

  @Override
  public String getEmailFolder() {
    return "koans";
  }
}
