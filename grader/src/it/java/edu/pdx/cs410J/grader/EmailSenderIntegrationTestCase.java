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

import javax.mail.Flags;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class EmailSenderIntegrationTestCase {
  protected final String emailServerHost = "127.0.0.1";
  protected final int smtpPort = 2525;
  protected final String imapUserName = "emailUser";
  protected final String imapPassword = "emailPassword";
  protected final int imapsPort = 9933;
  protected final File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
  protected GreenMail emailServer;
  private String graderEmail = EmailSender.TA_EMAIL.getAddress();

  @Before
  public void startEmailServer() throws FolderException, AuthorizationException {
    ServerSetup smtp = new ServerSetup(smtpPort, emailServerHost, ServerSetup.PROTOCOL_SMTP);
    ServerSetup imaps = new ServerSetup(imapsPort, emailServerHost, ServerSetup.PROTOCOL_IMAPS);
    emailServer = new GreenMail(new ServerSetup[]{ smtp, imaps });

    GreenMailUser graderUser = emailServer.setUser(graderEmail, imapUserName, imapPassword);

    moveEmailsFromInboxToProjectSubmissions(graderUser);

    emailServer.start();
  }

  protected void moveEmailsFromInboxToProjectSubmissions(GreenMailUser user) throws AuthorizationException, FolderException {
    ImapHostManager manager = emailServer.getManagers().getImapHostManager();
    MailFolder submissions = manager.createMailbox(user, getEmailFolderName());
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

  @After
  public void stopEmailServer() {
    emailServer.stop();
  }

  protected File createEmptyFile(File dir, String fileName) throws IOException {
    File file = new File(dir, fileName);
    FileWriter writer = new FileWriter(file);
    writer.write("");
    return file;
  }

  protected File createDirectories(String... directoryNames) {
    File dir = tempDirectory;
    for (String dirName : directoryNames) {
      dir = new File(dir, dirName);
      dir.mkdir();
    }

    return dir;
  }

  protected abstract String getEmailFolderName();
}
