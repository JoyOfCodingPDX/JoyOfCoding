package edu.pdx.cs410J.grader;

import java.io.*;

public class DownloadProjectSubmissions {

  public static void main(String[] args) {
    String passwordFile = null;
    for (String arg : args) {
      if (passwordFile == null) {
        passwordFile = arg;
      }
    }

    if (passwordFile == null) {
      usage("Missing password file");
    }

    String password = null;
    try {
      password = readGraderEmailPasswordFromFile(passwordFile);

    } catch (IOException e) {
      usage("Exception while reading password file");
    }

    GraderEmailAccount account = new GraderEmailAccount(password);
    account.fetchAttachmentsFromUnreadMessagesInFolder("Project Submissions", new EmailAttachmentProcessor() {

    });
  }

  private static String readGraderEmailPasswordFromFile(String passwordFile) throws IOException {
    File file = new File(passwordFile);
    if (!file.exists()) {
      usage("Password file \"" + passwordFile + "\" does not exist");
    }

    BufferedReader br = new BufferedReader(new FileReader(file));
    return br.readLine();
  }

  private static void usage(String message) {
    PrintStream err = System.err;
    err.println("** " + message);
    err.println();
    err.println("DownloadProjectSubmissions passwordFile");
    err.println("  passwordFile   File containing the password for the Grader's email account");
    err.println();

    System.exit(1);
  }
}
