package edu.pdx.cs.joy.grader;

import com.icegreen.greenmail.imap.AuthorizationException;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.user.GreenMailUser;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SendAndReceiveMultipartWithGreenmailIT extends GreenmailIntegrationTestCase {

  private final String emailFolderName = ProjectSubmissionsProcessor.EMAIL_FOLDER_NAME;

  @Override
  protected void doSomethingWithUser(GreenMailUser user) throws FolderException, AuthorizationException {
    moveEmailsFromInboxToFolder(user, emailFolderName);
  }


  @Test
  public void sendAndFetchMailMessageWithMultipleAttachments(@TempDir File tempDir) throws IOException, MessagingException, GeneralSecurityException {
    sendMailWithAttachedZipFile(tempDir);
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

  private void sendMailWithAttachedZipFile(File tempDir) throws IOException, MessagingException {
    File tempFile = new File(tempDir, "TempFile.java");
    tempFile.createNewFile();
    File[] filesToSubmit = new File[]{tempFile};
    File zipFile = new File(tempDir, "TempFile.zip");
    mailTA(makeZipFile(zipFile, filesToSubmit));
  }

  private File makeZipFile(File zipFile, File[] files) throws IOException {
    Map<File, String> sourceFilesAndNames =
      Arrays.stream(files).collect(Collectors.toMap(file -> file, File::getName));

    ZipFileOfFilesMaker maker = new ZipFileOfFilesMaker(sourceFilesAndNames, zipFile, new HashMap<>());
    maker.makeZipFile();

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
