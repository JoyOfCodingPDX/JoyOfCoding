package edu.pdx.cs410J.grader;

import com.icegreen.greenmail.imap.AuthorizationException;
import com.icegreen.greenmail.imap.ImapHostManager;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.store.FolderListener;
import com.icegreen.greenmail.store.MailFolder;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.mail.Flags;
import javax.mail.MessagingException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SubmitIT {

  private final String studentEmail = "student@email.com";
  private final String studentName = "Student Name";
  private final Collection<File> filesToSubmit = new ArrayList<>();
  private final String studentLoginId = "student";
  private final String projectName = "Project";
  private GreenMail emailServer;
  private final String emailServerHost = "127.0.0.1";
  private final int smtpPort = 2525;
  private final File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
  private final String imapUserName = "emailUser";
  private final String imapPassword = "emailPassword";
  private final int imapsPort = 9933;
  private String graderEmail = Submit.TA_EMAIL;

  @Before
  public void createFilesToSubmit() throws IOException {
    File dir = createDirectoriesWithFilesToSubmit("edu", "pdx", "cs410J", studentLoginId);
    List<String> fileNames = Arrays.asList(projectName + ".java", "File1.java", "File2.java");
    for (String fileName : fileNames) {
      this.filesToSubmit.add(createEmptyFile(dir, fileName));
    }
  }

  @Before
  public void startEmailServer() throws FolderException, AuthorizationException {
    ServerSetup smtp = new ServerSetup(smtpPort, emailServerHost, ServerSetup.PROTOCOL_SMTP);
    ServerSetup imaps = new ServerSetup(imapsPort, emailServerHost, ServerSetup.PROTOCOL_IMAPS);
    emailServer = new GreenMail(new ServerSetup[]{ smtp, imaps });

    GreenMailUser graderUser = emailServer.setUser(graderEmail, imapUserName, imapPassword);

    moveEmailsFromInboxToProjectSubmissions(graderUser);

    emailServer.start();
  }

  private void moveEmailsFromInboxToProjectSubmissions(GreenMailUser user) throws AuthorizationException, FolderException {
    ImapHostManager manager = emailServer.getManagers().getImapHostManager();
    MailFolder submissions = manager.createMailbox(user, ProjectSubmissionsProcessor.EMAIL_FOLDER_NAME);
    MailFolder inbox = manager.getInbox(user);
    inbox.addListener(new FolderListener() {
      @Override
      public void expunged(int msn) {

      }

      @Override
      public void added(int msn) {
        try {
          inbox.copyMessage(msn, submissions);

        } catch (FolderException ex) {
          throw new IllegalStateException("Can't copy message to submissions folder", ex);
        }
      }

      @Override
      public void flagsUpdated(int msn, Flags flags, Long uid) {

      }

      @Override
      public void mailboxDeleted() {

      }
    });
  }

  @Before
  public void enableDebugLogging() {
    GraderTools.setLoggingLevelToDebug();
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
    File dir = tempDirectory;
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
    submit.setUserEmail(studentEmail);
    submit.setUserId(studentLoginId);
    submit.setUserName(studentName);

    for (File file : filesToSubmit) {
      submit.addFile(file.getAbsolutePath());
    }

    submit.setEmailServerHostName(emailServerHost);
    submit.setEmailServerPort(smtpPort);
    submit.setDebug(true);
    submit.setSendReceipt(false);
    submit.submit(false);

    GradeBook gradeBook = new GradeBook("SubmitIT");
    gradeBook.addStudent(new Student(studentLoginId));
    gradeBook.addAssignment(new Assignment(projectName, 3.5));

    GraderEmailAccount account = new GraderEmailAccount(emailServerHost, imapsPort, imapUserName, imapPassword, true);
    FetchAndProcessGraderEmail.fetchAndProcessGraderEmails("projects", account, this.tempDirectory, gradeBook);

    Grade grade = gradeBook.getStudent(studentLoginId).get().getGrade(projectName);
    assertThat(grade, is(notNullValue()));
    assertThat(grade.getScore(), equalTo(Grade.NO_GRADE));
    assertThat(grade.getSubmissionTimes().size(), equalTo(1));
  }
}
