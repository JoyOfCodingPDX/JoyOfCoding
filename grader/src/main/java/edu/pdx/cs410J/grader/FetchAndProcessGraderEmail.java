package edu.pdx.cs410J.grader;

import java.io.*;

public class FetchAndProcessGraderEmail {

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

      @Override
      public void processAttachment(String fileName, InputStream inputStream) {
        System.out.println("    File name: " + fileName);
        System.out.println("    InputStream: " + inputStream);
      }
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
    err.println("FetchAndProcessGraderEmail passwordFile");
    err.println("  passwordFile   File containing the password for the Grader's email account");
    err.println();

    System.exit(1);
  }
}
