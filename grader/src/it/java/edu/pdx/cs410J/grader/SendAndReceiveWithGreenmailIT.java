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

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SendAndReceiveWithGreenmailIT {

  private final String studentEmail = "student@email.com";
  private final String studentLoginId = "student";
  private final String projectName = "Project";
  private GreenMail emailServer;
  private final String emailServerHost = "127.0.0.1";
  private final int smtpPort = 2525;
  private final File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
  private final String imapUserName = "emailUser";
  private final String imapPassword = "emailPassword";
  private final int imapsPort = 9933;

  @Before
  public void startEmailServer() throws FolderException, AuthorizationException {
    ServerSetup smtp = new ServerSetup(smtpPort, emailServerHost, ServerSetup.PROTOCOL_SMTP);
    ServerSetup imaps = new ServerSetup(imapsPort, emailServerHost, ServerSetup.PROTOCOL_IMAPS);
    emailServer = new GreenMail(new ServerSetup[]{ smtp, imaps });

    GreenMailUser user = emailServer.setUser(studentEmail, imapUserName, imapPassword);

    moveEmailsFromInboxToProjectSubmissions(user);

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
    System.setProperty("mail.imap.parse.debug", Boolean.TRUE.toString());
  }

  @After
  public void stopEmailServer() {
    emailServer.stop();
  }

  @Test
  public void sendAndFetchMailMessageWithInlineAttachment() throws IOException, MessagingException {
    sendMailMessageWithInlineAttachment();

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

  protected MimeMessage newEmailTo(Session session, String recipient, String subject) throws MessagingException {
    // Make a new email message
    MimeMessage message = new MimeMessage(session);

    InternetAddress[] to = {new InternetAddress(recipient)};
    message.setRecipients(Message.RecipientType.TO, to);
    message.setSubject(subject);
    return message;
  }

  protected Session newEmailSession(boolean debug) {
    // Obtain a Session for sending email
    Properties props = new Properties();
    props.put("mail.smtp.host", emailServerHost);
    props.put("mail.smtp.port", smtpPort);
    Session session = Session.getDefaultInstance(props, null);
    session.setDebug(debug);
    return session;
  }

  private void sendMailMessageWithInlineAttachment() throws MessagingException {
    MimeMessage message = newEmailTo(newEmailSession(true), studentEmail, "Message with inline attachment");

    MimeBodyPart textPart = createTextPartOfEmail();
    MimeBodyPart filePart = createZipAttachment();

    Multipart mp = new MimeMultipart();
    mp.addBodyPart(textPart);
    mp.addBodyPart(filePart);

    message.setContent(mp);

    Transport.send(message);
  }

  private MimeBodyPart createZipAttachment() throws MessagingException {
    // Now attach the Zip file
    DataSource ds = new FakeZipFileDataSource();
    DataHandler dh = new DataHandler(ds);
    MimeBodyPart filePart = new MimeBodyPart();

    String zipFileTitle = "ZipFile.zip";

    filePart.setDataHandler(dh);
    filePart.setFileName(zipFileTitle);
    filePart.setDescription("Zip file attachment");

    return filePart;

  }

  private MimeBodyPart createTextPartOfEmail() throws MessagingException {
    MimeBodyPart textPart = new MimeBodyPart();
    textPart.setContent("This is some text to be displayed inline", "text/plain");

    // Try not to display text as separate attachment
    textPart.setDisposition("inline");
    return textPart;
  }

  private static class FakeZipFileDataSource implements DataSource {
    final String text = "Not a valid zip file";

    @Override
    public InputStream getInputStream() throws IOException {
      return new ByteArrayInputStream(text.getBytes());
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
      throw new UnsupportedOperationException("This method is not implemented yet");
    }

    @Override
    public String getContentType() {
      return "application/zip";
    }

    @Override
    public String getName() {
      return "Not really a zip file";
    }
  }
}
