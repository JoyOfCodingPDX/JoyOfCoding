package edu.pdx.cs410J.grader;

import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SurveySubmissionsProcessor extends StudentEmailAttachmentProcessor {
  public SurveySubmissionsProcessor(File directory, GradeBook gradeBook) {
    super(directory, gradeBook);
  }

  @Override
  public String getEmailFolder() {
    return "Student Surveys";
  }

  @Override
  public void processAttachment(String fileName, InputStream inputStream) {
    System.out.println("    File name: " + fileName);
    System.out.println("    InputStream: " + inputStream);

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
    System.out.println("File \"" + file + "\" already exists");
  }

}
