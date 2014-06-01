package edu.pdx.cs410J.grader;

import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

class ProjectSubmissionsProcessor extends StudentEmailAttachmentProcessor {

  public ProjectSubmissionsProcessor(File directory, GradeBook gradeBook) {
    super(directory, gradeBook);
  }

  @Override
  public void processAttachment(String fileName, InputStream inputStream) {
    warn("    File name: " + fileName);
    warn("    InputStream: " + inputStream);

    try {
      File file = new File(directory, fileName);

      if (file.exists()) {
        warnOfPreExistingFile(file);
      }

      ByteStreams.copy(inputStream, new FileOutputStream(file));
    } catch (IOException ex) {
      logException("While writing \"" + fileName + "\" to \"" + directory + "\"", ex);
    }
  }

  private void warnOfPreExistingFile(File file) {
    warn("Overwriting existing file \"" + file + "\"");
  }

  @Override
  public String getEmailFolder() {
    return "Project Submissions";
  }
}
