package edu.pdx.cs410J.grader;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
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


    try {
      folder.close(false);
      store.close();

    } catch (MessagingException ex) {
      throw new IllegalStateException("While closing folder and store", ex);
    }
  }

  private void printFolderInformation(Folder folder) {
    try {
      PrintStream out = System.out;
      out.println("Folder: " + folder.getFullName());
      out.println("Message count: " + folder.getMessageCount());
      out.println("Unread messages: " + folder.getUnreadMessageCount());

    } catch (MessagingException ex) {
      throw new IllegalStateException("While getting folder information", ex);
    }
  }

  private Folder openProjectSubmissionsFolder(Store store) {
    try {
      Folder folder = store.getDefaultFolder();
      checkForValidFolder(folder, "Default");
      folder = folder.getFolder("INBOX");
      folder.open(Folder.READ_ONLY);
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
