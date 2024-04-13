package edu.pdx.cs.joy.grader;

import jakarta.mail.*;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.Test;

import java.security.GeneralSecurityException;

public class SendAndReceiveWithGreenmailIT extends GreenmailIntegrationTestCase {

  @Test
  public void sendAndFetchMailMessageWithInlineAttachment() throws MessagingException, GeneralSecurityException {
    sendMailMessageWithInlineAttachment();
    fetchEmailWithInlineAttachment();
  }

  private void fetchEmailWithInlineAttachment() throws MessagingException, GeneralSecurityException {
    Store store = connectToIMAPServer();
    Folder folder = openFolder(store, "INBOX");

    Message[] messages = folder.getMessages();

    FetchProfile profile = new FetchProfile();
    profile.add(FetchProfile.Item.ENVELOPE);
    profile.add(FetchProfile.Item.FLAGS);

    folder.fetch(messages, profile);

    for (Message message : messages) {
      System.out.println("  Content Type: " + message.getContentType());
    }
  }

  private void sendMailMessageWithInlineAttachment() throws MessagingException {
    MimeMessage message = newEmailTo(newEmailSession(true), emailAddress, "Message with inline attachment");

    MimeBodyPart textPart = createTextPartOfEmail();

    Multipart mp = new MimeMultipart();
    mp.addBodyPart(textPart);

    message.setContent(mp);

    Transport.send(message);
  }

  private MimeBodyPart createTextPartOfEmail() throws MessagingException {
    MimeBodyPart textPart = new MimeBodyPart();
    textPart.setContent("This is some text to be displayed inline", "text/plain");

    // Try not to display text as separate attachment
    textPart.setDisposition("inline");
    return textPart;
  }

}
