package edu.pdx.cs410J.grader;

import javax.mail.*;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class GraderEmailAccount {
  private final Logger logger = Logger.getLogger(this.getClass().getPackage().getName());

  private final String password;

  public GraderEmailAccount(String password) {
    this.password = password;
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
            processAttachment(message, processor);
          } else {
            warnOfUnexpectedMessage(message, "Fetched a message that wasn't multipart");
          }
        }
      }

    } catch (MessagingException | IOException ex ) {
      throw new IllegalStateException("While printing unread messages", ex);
    }
  }

  private void processAttachment(Message message, EmailAttachmentProcessor processor) throws MessagingException, IOException {
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

    if (parts.getCount() == 1) {
      warnOfUnexpectedMessage(message, "Fetched a message that only had one part");
      processAttachmentFromPart(message, parts.getBodyPart(0), processor);
    }

    if (parts.getCount() == 2) {
      processAttachmentFromPart(message, parts.getBodyPart(1), processor);
    }

    if (parts.getCount() > 2) {
      warnOfUnexpectedMessage(message, "Fetched a message with more than two parts");
      processAttachmentFromPart(message, parts.getBodyPart(1), processor);
    }
  }

  private void processAttachmentFromPart(Message message, BodyPart part, EmailAttachmentProcessor processor) throws MessagingException, IOException {
    String fileName = part.getFileName();
    if (fileName == null) {
      debug("    Skipping message with attachment without a name");
      return;
    }
    processor.processAttachment(message, fileName, part.getInputStream());
      debug("    " + part.getContentType());
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

  private StringBuilder addresses(Address[] addresses) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < addresses.length; i++) {
      Address address = addresses[i];
      sb.append(address.toString());
      if (i > addresses.length - 1) {
        sb.append(", ");
      }
    }

    return sb;
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
    this.logger.fine(message);
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

  private Store connectToGmail() {
    Properties props = new Properties();
    Session session = Session.getInstance(props, null);
    try {
      Store store = session.getStore("imaps");
      store.connect("imap.gmail.com", "sjavata", this.password);
      return store;

    } catch (MessagingException ex) {
      throw new IllegalStateException("While connecting to Gmail", ex);
    }
  }

  public void fetchAttachmentsFromUnreadMessagesInFolder(String folderName, EmailAttachmentProcessor processor) {
    Store store = connectToGmail();
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
