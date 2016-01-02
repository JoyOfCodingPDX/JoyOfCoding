package edu.pdx.cs410J.grader;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SubmitIT {

  private final String userEmail = "test@email.com";
  private final String userName = "Student Name";
  private Collection<File> filesToSubmit = new ArrayList<>();
  private final String userId = "student";
  private final String projectName = "Project";
  private GreenMail emailServer;
  private String emailServerHost = "127.0.0.1";
  private final int smtpPort = 2525;

  @Before
  public void createFilesToSubmit() throws IOException {
    File dir = createDirectoriesWithFilesToSubmit("edu", "pdx", "cs410J", userId);
    List<String> fileNames = Arrays.asList(projectName + ".java", "File1.java", "File2.java");
    for (String fileName : fileNames) {
      this.filesToSubmit.add(createEmptyFile(dir, fileName));
    }
  }

  @Before
  public void startEmailServer() {
    ServerSetup smtp = new ServerSetup(smtpPort, emailServerHost, ServerSetup.PROTOCOL_SMTP);
    emailServer = new GreenMail(smtp);
    emailServer.start();
  }

  @After
  public void deleteFilesToSubmit() {
    filesToSubmit.forEach(File::delete);
  }

  @After
  public void stopEmailServer() {
    emailServer.stop();
  }

  private File createEmptyFile(File dir, String fileName) throws IOException {
    File file = new File(dir, fileName);
    FileWriter writer = new FileWriter(file);
    writer.write("");
    return file;
  }

  private File createDirectoriesWithFilesToSubmit(String... directoryNames) {
    File dir = new File(System.getProperty("java.io.tmpdir"));
    for (String dirName : directoryNames) {
      dir = new File(dir, dirName);
      dir.mkdir();
    }

    return dir;
  }

  @Test
  public void submitFilesAndDownloadSubmission() throws IOException, MessagingException {
    Submit submit = new Submit();
    submit.setProjectName(projectName);
    submit.setUserEmail(userEmail);
    submit.setUserId(userId);
    submit.setUserName(userName);

    for (File file : filesToSubmit) {
      submit.addFile(file.getAbsolutePath());
    }

    submit.setEmailServerHostName(emailServerHost);
    submit.setEmailServerPort(smtpPort);
    submit.setDebug(true);
    submit.submit(false);
  }
}
