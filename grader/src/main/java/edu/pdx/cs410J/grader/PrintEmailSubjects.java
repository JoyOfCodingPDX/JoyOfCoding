package edu.pdx.cs410J.grader;

import java.io.*;
import java.util.*;
import javax.mail.*;

/**
 * This program reads your INBOX and prints out the subjects of each
 * of the emails.
 */
public class PrintEmailSubjects {
  private static final PrintStream out = System.out;
  private static final PrintStream err = System.err;

  /**
   * Prints usage information about this program
   */
  private static void usage(String s) {
    err.println("\n** " + s + "\n");
    err.println("usage: java PrintEmailSubjects mailbox");
    err.println("");
    System.exit(1);
  }

  public static void main(String[] args) throws Throwable {
    String mailbox = null;
    boolean DEBUG = false;

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-debug")) {
        DEBUG = true;

      } else if (mailbox == null) {
        mailbox = args[i];

      } else {
        usage("Spurious command line: " + args);
      }
    }

    if (mailbox == null) {
      usage("Missing mailbox");
    }

    // Get a connection to the mail server
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);
    session.setDebug(DEBUG);

    Provider[] providers = session.getProviders();
    for (int i = 0; i < providers.length; i++) {
      Provider provider = providers[i];
      out.println(provider);
    }

    Store store = session.getStore("imap");
    Folder folder = store.getFolder(mailbox);
    Message[] messages = folder.getMessages();
    out.println("Folder " + folder.getName() + " with " +
                messages.length + " messages");
    for (int i = 0; i < messages.length; i++) {
      Message message = messages[i];
      out.println("  " + i + ": " + message.getSubject());
    }

    out.println("");

  }

}
