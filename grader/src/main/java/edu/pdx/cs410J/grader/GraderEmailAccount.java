package edu.pdx.cs410J.grader;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import java.util.Properties;

public class GraderEmailAccount {
  private final String password;

  public GraderEmailAccount(String password) {
    this.password = password;
  }


  public void fetchProjectSubmissions() {
    Store store = connectToGmail();

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
