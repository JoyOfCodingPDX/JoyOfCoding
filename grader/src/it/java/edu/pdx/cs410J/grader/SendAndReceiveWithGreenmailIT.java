package edu.pdx.cs410J.grader;

import com.icegreen.greenmail.imap.AuthorizationException;
import com.icegreen.greenmail.imap.ImapHostManager;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.store.FolderListener;
import com.icegreen.greenmail.store.MailFolder;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.sun.mail.util.MailSSLSocketFactory;
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
import java.security.GeneralSecurityException;
import java.util.Properties;

public class SendAndReceiveWithGreenmailIT {

  private final String studentEmail = "student@email.com";
  private final String emailFolderName = ProjectSubmissionsProcessor.EMAIL_FOLDER_NAME;
  private GreenMail emailServer;
  private final String emailServerHost = "127.0.0.1";
  private final int smtpPort = 2525;
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
    MailFolder submissions = manager.createMailbox(user, emailFolderName);
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
  public void sendAndFetchMailMessageWithInlineAttachment() throws IOException, MessagingException, GeneralSecurityException {
    sendMailMessageWithInlineAttachment();
    fetchEmailWithInlineAttachment();
  }

  private void fetchEmailWithInlineAttachment() throws MessagingException, GeneralSecurityException {
    Store store = connectToIMAPServer();
    Folder folder = openFolder(store, emailFolderName);

    Message[] messages = folder.getMessages();

    FetchProfile profile = new FetchProfile();
    profile.add(FetchProfile.Item.ENVELOPE);
    profile.add(FetchProfile.Item.FLAGS);

    folder.fetch(messages, profile);

    for (Message message : messages) {
      if (isUnread(message)) {
        printMessageDetails(message);
      }
    }
  }

  private void debug(String s) {
    System.out.println(s);
  }

  private void printMessageDetails(Message message) throws MessagingException {
    debug("  To: " + addresses(message.getRecipients(Message.RecipientType.TO)));
    debug("  From: " + addresses(message.getFrom()));
    debug("  Subject: " + message.getSubject());
    debug("  Sent: " + message.getSentDate());
    debug("  Flags: " + flags(message.getFlags()));
    debug("  Content Type: " + message.getContentType());
  }

  private StringBuilder flags(Flags flags) {
    StringBuilder sb = new StringBuilder();
    systemFlags(flags, sb);
    return sb;
  }

  private void systemFlags(Flags flags, StringBuilder sb) {
    Flags.Flag[] systemFlags = flags.getSystemFlags();
    for (int i = 0; i < systemFlags.length; i++) {
      Flags.Flag flag = systemFlags[i];
      if (flag == Flags.Flag.ANSWERED) {
        sb.append("ANSWERED");

      } else if (flag == Flags.Flag.DELETED) {
        sb.append("DELETED");

      } else if (flag == Flags.Flag.DRAFT) {
        sb.append("DRAFT");

      } else if (flag == Flags.Flag.FLAGGED) {
        sb.append("FLAGGED");

      } else if (flag == Flags.Flag.RECENT) {
        sb.append("RECENT");

      } else if (flag == Flags.Flag.SEEN) {
        sb.append("SEEN");

      } else if (flag == Flags.Flag.USER) {
        sb.append("USER");

      } else {
        sb.append("UNKNOWN");
      }

      if (i > systemFlags.length - 1) {
        sb.append(", ");
      }
    }
  }

  private String addresses(Address[] addresses) {
    if (addresses == null) {
      return "<None>";
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < addresses.length; i++) {
      Address address = addresses[i];
      sb.append(address.toString());
      if (i > addresses.length - 1) {
        sb.append(", ");
      }
    }

    return sb.toString();
  }

  private boolean isUnread(Message message) throws MessagingException {
    return !message.getFlags().contains(Flags.Flag.SEEN);
  }

  private Folder openFolder(Store store, String folderName) throws MessagingException {
    Folder folder = store.getDefaultFolder();
    folder = folder.getFolder(folderName);
    folder.open(Folder.READ_WRITE);
    return folder;
  }

  private Store connectToIMAPServer() throws GeneralSecurityException, MessagingException {
    Properties props = new Properties();

    MailSSLSocketFactory socketFactory = new MailSSLSocketFactory();
    socketFactory.setTrustedHosts(new String[]{"127.0.0.1", "localhost"});
    props.put("mail.imaps.ssl.socketFactory", socketFactory);

    Session session = Session.getInstance(props, null);
    Store store = session.getStore("imaps");
    store.connect(emailServerHost, imapsPort, imapUserName, imapPassword);

    return store;
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
