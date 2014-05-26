package edu.pdx.cs410J.grader;

import javax.mail.*;
import java.io.PrintStream;
import java.util.Properties;

public class GraderEmailAccount {
  private final String password;

  public GraderEmailAccount(String password) {
    this.password = password;
  }


  public void fetchProjectSubmissions() {
    Store store = connectToGmail();
    Folder folder = openProjectSubmissionsFolder(store);
    printFolderInformation(folder);
    printUnreadMessages(folder);


    try {
      folder.close(false);
      store.close();

    } catch (MessagingException ex) {
      throw new IllegalStateException("While closing folder and store", ex);
    }
  }

  private void printUnreadMessages(Folder folder) {
    try {
      int unreadMessageCount = folder.getUnreadMessageCount();
      Message[] messages = folder.getMessages();

      FetchProfile profile = new FetchProfile();
      profile.add(FetchProfile.Item.ENVELOPE);
      profile.add(FetchProfile.Item.FLAGS);

      folder.fetch(messages, profile);

      for (Message message : messages) {
        printMessageInformation(message);
      }

    } catch (MessagingException ex) {
      throw new IllegalStateException("While printing unread messages", ex);
    }
  }

  private void printMessageInformation(Message message) throws MessagingException {
    PrintStream out = System.out;
    out.println("Message");
    out.println("  To: " + addresses(message.getRecipients(Message.RecipientType.TO)));
    out.println("  From: " + addresses(message.getFrom()));
    out.println("  Subject: " + message.getSubject());

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
      PrintStream out = System.out;
      out.println("Folder: " + folder.getFullName());
      out.println("Message count: " + folder.getMessageCount());
      out.println("Unread messages: " + folder.getUnreadMessageCount());
      out.println("New messages: " + folder.getNewMessageCount());

    } catch (MessagingException ex) {
      throw new IllegalStateException("While getting folder information", ex);
    }
  }

  private Folder openProjectSubmissionsFolder(Store store) {
    try {
      Folder folder = store.getDefaultFolder();
      checkForValidFolder(folder, "Default");
      folder = folder.getFolder("INBOX");
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
}
