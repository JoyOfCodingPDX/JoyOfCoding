package edu.pdx.cs410J.grader;

import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

class ProjectSubmissionsProcessor implements EmailAttachmentProcessor {

  private final File directory;

  public ProjectSubmissionsProcessor(File directory) {
    this.directory = directory;
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

  private void logException(String message, IOException ex) {
    System.err.println(message);
    ex.printStackTrace(System.err);
  }

  private void writeToFileToDirectory(File directory, String fileName, InputStream inputStream) throws IOException {
    File file = new File(directory, fileName);

    if (file.exists()) {
      warnOfPreExistingFile(file);
    }

    ByteStreams.copy(inputStream, new FileOutputStream(file));
  }

  private void warnOfPreExistingFile(File file) {
    System.out.println("File \"" + file + "\" already exists");
  }

  public String getEmailFolder() {
    return "Project Submissions";
  }
}
