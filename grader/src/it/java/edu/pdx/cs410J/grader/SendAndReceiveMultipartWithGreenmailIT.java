package edu.pdx.cs410J.grader;

import com.google.common.io.ByteStreams;
import com.icegreen.greenmail.imap.AuthorizationException;
import com.icegreen.greenmail.imap.ImapHostManager;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.store.FolderListener;
import com.icegreen.greenmail.store.MailFolder;
import com.icegreen.greenmail.user.GreenMailUser;
import org.junit.Test;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SendAndReceiveMultipartWithGreenmailIT extends GreenmailIntegrationTestCase {

  private final String emailFolderName = ProjectSubmissionsProcessor.EMAIL_FOLDER_NAME;

  @Override
  protected void doSomethingWithUser(GreenMailUser user) throws FolderException, AuthorizationException {
    moveEmailsFromInboxToProjectSubmissions(user);
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


  @Test
  public void sendAndFetchMailMessageWithMultipleAttachments() throws IOException, MessagingException, GeneralSecurityException {
    sendMailWithAttachedZipFile();
    fetchAttachmentsFromUnreadMessagesInFolder();
  }

  public void fetchAttachmentsFromUnreadMessagesInFolder() throws GeneralSecurityException, MessagingException {
    Store store = connectToIMAPServer();
    Folder folder = openFolder(store, emailFolderName);

    fetchAttachmentsFromUnreadMessagesInFolder(folder);

    try {
      folder.close(false);
      store.close();

    } catch (MessagingException ex) {
      throw new IllegalStateException("While closing folder and store", ex);
    }

  }

  private void fetchAttachmentsFromUnreadMessagesInFolder(Folder folder) throws MessagingException {
    Message[] messages = folder.getMessages();

    FetchProfile profile = new FetchProfile();
    profile.add(FetchProfile.Item.ENVELOPE);
    profile.add(FetchProfile.Item.FLAGS);

    folder.fetch(messages, profile);

    for (Message message : messages) {
      System.out.println(message.getContentType());
      assertThat(message.getContentType() + " is not multi-part", isMultipartMessage(message), equalTo(true));
    }
  }

  private boolean isMultipartMessage(Message message) throws MessagingException {
    return message.isMimeType("multipart/*");
  }

  private void sendMailWithAttachedZipFile() throws IOException, MessagingException {
    File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
    File tempFile = File.createTempFile("TempFile", ".java", tempDirectory);
    tempFile.createNewFile();
    File[] filesToSubmit = new File[]{tempFile};
    File zipFile = File.createTempFile("TempFile", ".zip", tempDirectory);
    mailTA(makeZipFile(zipFile, filesToSubmit));
  }

  public File makeZipFile(File zipFile, File[] files) throws IOException {
    ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
    zos.setMethod(ZipOutputStream.DEFLATED);

    // Add the source files to the Zip
    for (File file : files) {
      String fileName = file.getName();
      System.out.println("Adding " + fileName + " to zip");
      ZipEntry entry = new ZipEntry(fileName);
      entry.setTime(file.lastModified());
      entry.setSize(file.length());

      entry.setMethod(ZipEntry.DEFLATED);

      // Add the entry to the ZIP file
      zos.putNextEntry(entry);

      ByteStreams.copy(new FileInputStream(file), zos);

      zos.closeEntry();
    }

    zos.close();

    return zipFile;
  }

  private void mailTA(File zipFile) throws MessagingException {
    MimeMessage message = newEmailTo(newEmailSession(true), emailAddress, "Email subject");

    MimeBodyPart textPart = createTextPartOfTAEmail();
    MimeBodyPart filePart = createZipAttachment(zipFile);

    Multipart mp = new MimeMultipart();
    mp.addBodyPart(textPart);
    mp.addBodyPart(filePart);

    message.setContent(mp);

    Transport.send(message);
  }

  private MimeBodyPart createTextPartOfTAEmail() throws MessagingException {
    MimeBodyPart textPart = new MimeBodyPart();
    textPart.setContent("This is some text to be displayed inline", "text/plain");

    // Try not to display text as separate attachment
    textPart.setDisposition("inline");
    return textPart;
  }

  private MimeBodyPart createZipAttachment(File zipFile) throws MessagingException {
    // Now attach the Zip file
    DataSource ds = new FileDataSource(zipFile) {
      @Override
      public String getContentType() {
        return "application/zip";
      }
    };
    DataHandler dh = new DataHandler(ds);
    MimeBodyPart filePart = new MimeBodyPart();

    String zipFileTitle = "ZipFile.zip";

    filePart.setDataHandler(dh);
    filePart.setFileName(zipFileTitle);
    filePart.setDescription("Zip File");

    return filePart;
  }


}
