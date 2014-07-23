package edu.pdx.cs410J.grader;

import com.google.common.io.ByteStreams;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class KoansSubmissionsProcessor extends StudentEmailAttachmentProcessor{
  public KoansSubmissionsProcessor(File directory, GradeBook gradeBook) {
    super(directory, gradeBook);
  }

  @Override
  public void processAttachment(Message message, String fileName, InputStream inputStream) {
    String studentName;
    try {
      studentName = getStudentName(message);

    } catch (MessagingException ex) {
      logException("While getting student name", ex);
      return;
    }

    try {
      File file = getKoansFileLocation(fileName, studentName);
      warn("Writing \"" + fileName + "\" to " + file);

      ByteStreams.copy(inputStream, new FileOutputStream(file));
    } catch (IOException ex) {
      logException("While writing \"" + fileName + "\" to \"" + directory + "\"", ex);
    }
  }

  private File getKoansFileLocation(String fileName, String studentName) throws IOException {
    File dir = new File(directory, studentName);
    if (!dir.exists() && !dir.mkdirs()) {
      throw new IOException("Could not create directory \"" + dir + "\"");
    }
    return new File(dir, fileName);
  }

  private String getStudentName(Message message) throws MessagingException {
    Address from = message.getFrom()[0];
    if (from instanceof InternetAddress) {
      return getStudentName((InternetAddress) from);
    } else {
      return from.toString();
    }
  }

  private String getStudentName(InternetAddress from) {
    if (from.getPersonal() != null) {
      return from.getPersonal();

    } else {
      return from.getAddress();
    }
  }

  @Override
  public String getEmailFolder() {
    return "koans";
  }
}
