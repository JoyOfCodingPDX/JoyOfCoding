package edu.pdx.cs.joy.grader;

import com.icegreen.greenmail.imap.AuthorizationException;
import com.icegreen.greenmail.imap.ImapHostManager;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.store.FolderListener;
import com.icegreen.greenmail.store.MailFolder;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.sun.mail.util.MailSSLSocketFactory;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.security.GeneralSecurityException;
import java.util.Properties;

public class GreenmailIntegrationTestCase {
  protected final String emailAddress = "test@email.com";
  protected final String emailServerHost = "127.0.0.1";
  protected final int smtpPort = 2525;
  protected final String imapUserName = "emailUser";
  protected final String imapPassword = "emailPassword";
  protected final int imapsPort = 9933;
  protected GreenMail emailServer;

  @BeforeEach
  public void startEmailServer() throws FolderException, AuthorizationException {
    ServerSetup smtp = new ServerSetup(smtpPort, emailServerHost, ServerSetup.PROTOCOL_SMTP);
    ServerSetup imaps = new ServerSetup(imapsPort, emailServerHost, ServerSetup.PROTOCOL_IMAPS);
    emailServer = new GreenMail(new ServerSetup[]{ smtp, imaps });

    GreenMailUser user = emailServer.setUser(emailAddress, imapUserName, imapPassword);
    doSomethingWithUser(user);

    emailServer.start();
  }

  protected void doSomethingWithUser(GreenMailUser user) throws FolderException, AuthorizationException {

  }

  @BeforeEach
  public void enableDebugLogging() {
    System.setProperty("mail.imap.parse.debug", Boolean.TRUE.toString());
  }

  @AfterEach
  public void stopEmailServer() {
    emailServer.stop();
  }

  protected MimeMessage newEmailTo(Session session, String recipient, String subject) throws MessagingException {
    MimeMessage message = new MimeMessage(session);

    InternetAddress[] to = {new InternetAddress(recipient)};
    message.setRecipients(Message.RecipientType.TO, to);
    message.setSubject(subject);
    return message;
  }

  protected Session newEmailSession(boolean debug) {
    Properties props = new Properties();
    props.put("mail.smtp.host", emailServerHost);
    props.put("mail.smtp.port", smtpPort);
    Session session = Session.getDefaultInstance(props, null);
    session.setDebug(debug);
    return session;
  }

  protected Folder openFolder(Store store, String folderName) throws MessagingException {
    Folder folder = store.getDefaultFolder();
    folder = folder.getFolder(folderName);
    folder.open(Folder.READ_WRITE);
    return folder;
  }

  protected Store connectToIMAPServer() throws GeneralSecurityException, MessagingException {
    Properties props = new Properties();

    MailSSLSocketFactory socketFactory = new MailSSLSocketFactory();
    socketFactory.setTrustedHosts(new String[]{"127.0.0.1", "localhost"});
    props.put("mail.imaps.ssl.socketFactory", socketFactory);

    Session session = Session.getInstance(props, null);
    Store store = session.getStore("imaps");
    store.connect(emailServerHost, imapsPort, imapUserName, imapPassword);

    return store;
  }

  protected void moveEmailsFromInboxToFolder(GreenMailUser user, String folderName) throws AuthorizationException, FolderException {
    ImapHostManager manager = emailServer.getManagers().getImapHostManager();
    MailFolder submissions = manager.createMailbox(user, folderName);
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
}
