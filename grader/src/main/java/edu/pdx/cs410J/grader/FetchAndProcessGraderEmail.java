package edu.pdx.cs410J.grader;

import com.google.common.io.ByteStreams;

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
    account.fetchAttachmentsFromUnreadMessagesInFolder("Project Submissions", new EmailAttachmentProcessor() {

      @Override
      public void processAttachment(String fileName, InputStream inputStream) {
        System.out.println("    File name: " + fileName);
        System.out.println("    InputStream: " + inputStream);

        try {
          writeToFileToDirectory(directory, fileName, inputStream);
        } catch (IOException ex) {
          logException("While writing \"" + fileName + "\" to \"" + directory + "\"", ex);
        }
      }

      private void logException(String message, IOException ex) {
        System.err.println(message);
        ex.printStackTrace(System.err);
      }

      private void writeToFileToDirectory(File directory, String fileName, InputStream inputStream) throws IOException {
        File file = new File(directory, fileName);

        if (file.exists()) {
          warnOfPreExistingFile(file);
        }

        ByteStreams.copy(inputStream, new FileOutputStream(file));
      }

      private void warnOfPreExistingFile(File file) {
        System.out.println("File \"" + file + "\" already exists");
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
    err.println("FetchAndProcessGraderEmail passwordFile directory");
    err.println("  passwordFile   File containing the password for the Grader's email account");
    err.println("  directory      The directory in which to download attachments");
    err.println();

    System.exit(1);
  }
}
