package edu.pdx.cs410J.grader;

import java.io.*;

public class FetchAndProcessGraderEmail {

  public static void main(String[] args) {
    String passwordFile = null;
    String directoryName = null;

    for (String arg : args) {
      if (passwordFile == null) {
        passwordFile = arg;


      } else if (directoryName == null) {
        directoryName = arg;

      } else {
        usage("Extraneous argument: " + arg);
      }
    }

    if (passwordFile == null) {
      usage("Missing password file");
    }

    if (directoryName == null) {
      usage("Missing directory name");
    }

    String password = null;
    try {
      password = readGraderEmailPasswordFromFile(passwordFile);

    } catch (IOException e) {
      usage("Exception while reading password file");
    }

    assert directoryName != null;
    final File directory = new File(directoryName);
    if (!directory.exists()) {
      usage("Download directory \"" + directoryName + "\" does not exist");
    }

    if (!directory.isDirectory()) {
      usage("Download directory \"" + directoryName + "\" is not a directory");
    }

    GraderEmailAccount account = new GraderEmailAccount(password);
    ProjectSubmissionsProcessor processor = new ProjectSubmissionsProcessor(directory);
    account.fetchAttachmentsFromUnreadMessagesInFolder(processor.getEmailFolder(), processor);
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
    err.println("FetchAndProcessGraderEmail passwordFile directory");
    err.println("  passwordFile   File containing the password for the Grader's email account");
    err.println("  directory      The directory in which to download attachments");
    err.println();

    System.exit(1);
  }
}
