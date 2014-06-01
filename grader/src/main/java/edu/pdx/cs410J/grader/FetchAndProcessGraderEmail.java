package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;

import java.io.*;

public class FetchAndProcessGraderEmail {

  public static void main(String[] args) {
    String passwordFile = null;
    String directoryName = null;
    String gradeBookFile = null;

    for (String arg : args) {
      if (passwordFile == null) {
        passwordFile = arg;


      } else if (directoryName == null) {
        directoryName = arg;

      } else if (gradeBookFile == null) {
        gradeBookFile = arg;

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

    if (gradeBookFile == null) {
      usage("Missing grade book file");
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

    GradeBook gradeBook = null;
    try {
      gradeBook = readGradeBookFromFile(gradeBookFile);

    } catch (IOException ex) {
      usage("Couldn't read grade book file \"" + gradeBookFile + "\"");

    } catch (ParserException ex) {
      usage("Couldn't parse grade book file \"" + gradeBookFile + "\"");
    }

    GraderEmailAccount account = new GraderEmailAccount(password);
    ProjectSubmissionsProcessor processor = new ProjectSubmissionsProcessor(directory, gradeBook);
    account.fetchAttachmentsFromUnreadMessagesInFolder(processor.getEmailFolder(), processor);
  }

  private static GradeBook readGradeBookFromFile(String fileName) throws IOException, ParserException {
    XmlGradeBookParser parser = new XmlGradeBookParser(fileName);
    return parser.parse();
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
    err.println("  passwordFile    File containing the password for the Grader's email account");
    err.println("  directory       The directory in which to download attachments");
    err.println("  gradeBookFile   Grade Book XML file related to this submission");
    err.println();

    System.exit(1);
  }
}
