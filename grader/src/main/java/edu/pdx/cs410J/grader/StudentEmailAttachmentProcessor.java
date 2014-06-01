package edu.pdx.cs410J.grader;

import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

  protected void writeToFileToDirectory(File directory, String fileName, InputStream inputStream) throws IOException {
    File file = new File(directory, fileName);

    if (file.exists()) {
      warnOfPreExistingFile(file);
    }

    ByteStreams.copy(inputStream, new FileOutputStream(file));
  }

  private void warnOfPreExistingFile(File file) {
    System.out.println("File \"" + file + "\" already exists");
  }

  public abstract String getEmailFolder();
}
