package edu.pdx.cs410J.grader;

import com.google.common.annotations.VisibleForTesting;
import com.sun.mail.util.MailSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GraderEmailAccount {
  private final Logger logger = LoggerFactory.getLogger(this.getClass().getPackage().getName());

  private final String password;
  private final String userName;
  private final String emailServerHostName;
  private final int emailServerPort;
  private final boolean trustLocalhostSSL;

  public GraderEmailAccount(String password) {
    this("sjavata", password);
  }

  public GraderEmailAccount(String userName, String password) {
    this("imap.gmail.com", 993, userName, password, false);
  }

  @VisibleForTesting
  GraderEmailAccount(String emailServerHostName, int emailServerPort, String userName, String password, boolean trustLocalhostSSL) {
    this.userName = userName;
    this.password = password;
    this.emailServerHostName = emailServerHostName;
    this.emailServerPort = emailServerPort;
    this.trustLocalhostSSL = trustLocalhostSSL;
  }

  private void fetchAttachmentsFromUnreadMessagesInFolder(Folder folder, EmailAttachmentProcessor processor) {
    try {
      Message[] messages = folder.getMessages();

      FetchProfile profile = new FetchProfile();
      profile.add(FetchProfile.Item.ENVELOPE);
      profile.add(FetchProfile.Item.FLAGS);

      folder.fetch(messages, profile);

      for (Message message : messages) {
        if (isUnread(message)) {
          printMessageInformation(message);
          if (isMultipartMessage(message)) {
            processAttachments(message, processor);
          } else {
            warnOfUnexpectedMessage(message, "Fetched a message that wasn't multipart: " + message.getContentType());
          }
        }
      }

    } catch (MessagingException | IOException ex ) {
      throw new IllegalStateException("While printing unread messages", ex);
    }
  }

  private void processAttachments(Message message, EmailAttachmentProcessor processor) throws MessagingException, IOException {
    Multipart parts;
    try {
      parts = (Multipart) message.getContent();
    } catch (IOException ex) {
      throw new MessagingException("While getting content", ex);
    }

    debug("  Part count: " + parts.getCount());

    if (parts.getCount() <= 0) {
      warnOfUnexpectedMessage(message, "Fetched a message that has no attachments");
    }

    for (String contentType : processor.getSupportedContentTypes()) {
      for (BodyPart part : getBodyPartsInReverseOrder(parts)) {
        if (attemptToProcessPart(message, part, processor, contentType)) {
          return;
        }
      }
    }

    warnOfUnexpectedMessage(message, "Could not process of any attachments");
  }

  private boolean attemptToProcessPart(Message message, BodyPart part, EmailAttachmentProcessor processor, String supportedContentType) throws MessagingException, IOException {
    if (partHasContentType(part, supportedContentType)) {
      debug("    Processing attachment of type " + part.getContentType());
      processAttachmentFromPart(message, part, processor);
      return true;

    } else if (partIsMultiPart(part)) {
      debug("    Attempting to process attachment of type " + part.getContentType());

      for (BodyPart subpart : getBodyPartsInReverseOrder((Multipart) part.getContent())) {
        if (attemptToProcessPart(message, subpart, processor, supportedContentType)) {
          return true;
        }
      }

    } else {
      debug("    Skipping attachment of type " + part.getContentType());
    }
    return false;
  }

  private List<BodyPart> getBodyPartsInReverseOrder(Multipart parts) throws MessagingException {
    List<BodyPart> list = new ArrayList<>(parts.getCount());
    for (int i = parts.getCount() - 1; i >= 0; i--) {
      list.add(parts.getBodyPart(i));
    }

    return list;
  }

  private boolean partIsMultiPart(BodyPart part) throws IOException, MessagingException {
    return part.getContent() instanceof Multipart;
  }

  private boolean partHasContentType(BodyPart part, String contentType) throws MessagingException {
    return part.getContentType().toUpperCase().contains(contentType.toUpperCase());
  }

  private void processAttachmentFromPart(Message message, BodyPart part, EmailAttachmentProcessor processor) throws MessagingException, IOException {
    String fileName = part.getFileName();
    processor.processAttachment(message, fileName, part.getInputStream());
  }

  private void warnOfUnexpectedMessage(Message message, String description) throws MessagingException {
    debug(description);
    printMessageDetails(message);
  }

  private boolean isMultipartMessage(Message message) throws MessagingException {
    return message.isMimeType("multipart/*");
  }

  private boolean isUnread(Message message) throws MessagingException {
    return !message.getFlags().contains(Flags.Flag.SEEN);
  }

  private void printMessageInformation(Message message) throws MessagingException {
    debug("Message");
    printMessageDetails(message);
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

  private void printFolderInformation(Folder folder) {
    try {
      debug("Folder: " + folder.getFullName());
      debug("Message count: " + folder.getMessageCount());
      debug("Unread messages: " + folder.getUnreadMessageCount());
      debug("New messages: " + folder.getNewMessageCount());

    } catch (MessagingException ex) {
      throw new IllegalStateException("While getting folder information", ex);
    }
  }

  private void debug(String message) {
    this.logger.debug(message);
  }

  private Folder openFolder(Store store, String folderName) {
    try {
      Folder folder = store.getDefaultFolder();
      checkForValidFolder(folder, "Default");
      folder = folder.getFolder(folderName);
      folder.open(Folder.READ_WRITE);
      return folder;

    } catch (MessagingException ex) {
      throw new IllegalStateException("While opening project submissions folder", ex);
    }
  }

  private void checkForValidFolder(Folder folder, String folderName) {
    if (folder == null) {
      throw new IllegalStateException("Folder \"" + folderName + "\" does not exist");
    }

  }

  private Store connectToIMAPServer() {
    try {
      Properties props = new Properties();

      if (this.trustLocalhostSSL) {
        MailSSLSocketFactory socketFactory= new MailSSLSocketFactory();
        socketFactory.setTrustedHosts(new String[] { "127.0.0.1", "localhost" });
        props.put("mail.imaps.ssl.socketFactory", socketFactory);
      }

      Session session = Session.getInstance(props, null);
      Store store = session.getStore("imaps");
      store.connect(this.emailServerHostName, this.emailServerPort, this.userName, this.password);
      return store;

    } catch (MessagingException | GeneralSecurityException ex) {
      throw new IllegalStateException("While connecting to " + this.emailServerHostName + ":" + this.emailServerPort, ex);
    }
  }

  public void fetchAttachmentsFromUnreadMessagesInFolder(String folderName, EmailAttachmentProcessor processor) {
    Store store = connectToIMAPServer();
    Folder folder = openFolder(store, folderName);
    printFolderInformation(folder);

    fetchAttachmentsFromUnreadMessagesInFolder(folder, processor);

    try {
      folder.close(false);
      store.close();

    } catch (MessagingException ex) {
      throw new IllegalStateException("While closing folder and store", ex);
    }

  }
}
