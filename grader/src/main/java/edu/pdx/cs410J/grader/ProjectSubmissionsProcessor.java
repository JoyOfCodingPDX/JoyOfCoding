package edu.pdx.cs410J.grader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

class ProjectSubmissionsProcessor extends StudentEmailAttachmentProcessor {

  public ProjectSubmissionsProcessor(File directory, GradeBook gradeBook) {
    super(directory, gradeBook);
  }

  @Override
  public void processAttachment(String fileName, InputStream inputStream) {
    System.out.println("    File name: " + fileName);
    System.out.println("    InputStream: " + inputStream);

    try {
      writeToFileToDirectory(directory, fileName, inputStream);
    } catch (IOException ex) {
      logException("While writing \"" + fileName + "\" to \"" + directory + "\"", ex);
    }
  }

  @Override
  public String getEmailFolder() {
    return "Project Submissions";
  }
}
