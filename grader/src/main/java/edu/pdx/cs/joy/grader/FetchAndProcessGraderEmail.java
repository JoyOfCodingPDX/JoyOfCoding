package edu.pdx.cs.joy.grader;

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.grader.gradebook.GradeBook;
import edu.pdx.cs.joy.grader.gradebook.XmlDumper;
import edu.pdx.cs.joy.grader.gradebook.XmlGradeBookParser;

import java.io.*;

public class FetchAndProcessGraderEmail {

  public static void main(String[] args) {
    String passwordFile = null;
    String directoryName = null;
    String gradeBookFile = null;
    String whatToFetch = null;

    for (String arg : args) {
      if (passwordFile == null) {
        passwordFile = arg;

      } else if (directoryName == null) {
        directoryName = arg;

      } else if (gradeBookFile == null) {
        gradeBookFile = arg;

      } else if (whatToFetch == null) {
        whatToFetch = arg;

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

    if (whatToFetch == null) {
      usage("Missing kind of email to fetch");
    }

    String password = readGraderEmailPasswordFromFile(passwordFile);
    File directory = getDirectory(directoryName);
    GradeBook gradeBook = getGradeBook(gradeBookFile);

    GraderEmailAccount account = new GraderEmailAccount("sjavata", password, m -> { });
    fetchAndProcessGraderEmails(whatToFetch, account, directory, gradeBook);

    saveGradeBookIfModified(gradeBook, gradeBookFile);
  }

  @VisibleForTesting
  static void fetchAndProcessGraderEmails(String whatToFetch, GraderEmailAccount account, File directory, GradeBook gradeBook) {
    StudentEmailAttachmentProcessor processor = getStudentEmailAttachmentProcessor(whatToFetch, directory, gradeBook);
    account.fetchAttachmentsFromUnreadMessagesInFolder(processor.getEmailFolder(), processor);
  }

  private static void saveGradeBookIfModified(GradeBook gradeBook, String gradeBookFile) {
    if (gradeBook.isDirty()) {
      File file = new File(gradeBookFile);
      try {
        XmlDumper dumper = new XmlDumper(file);
        dumper.dump(gradeBook);

      } catch (IOException e) {
        usage("Can't write grade book in \"" + gradeBookFile + "\"");
      }
    }
  }

  private static StudentEmailAttachmentProcessor getStudentEmailAttachmentProcessor(String whatToFetch, File directory, GradeBook gradeBook) {
    if (whatToFetch.equalsIgnoreCase("projects")) {
      return new ProjectSubmissionsProcessor(directory, gradeBook);

    } else if (whatToFetch.equalsIgnoreCase("surveys")) {
      return new SurveySubmissionsProcessor(directory, gradeBook);

    } else if (whatToFetch.equalsIgnoreCase("androidProjects")) {
          return new AndroidProjectSubmissionsProcessor(directory, gradeBook);

    } else {
      return usage("Cannot fetch \"" + whatToFetch + "\"");
    }
  }

  private static GradeBook getGradeBook(String gradeBookFile) {
    try {
      XmlGradeBookParser parser = new XmlGradeBookParser(gradeBookFile);
      return parser.parse();

    } catch (IOException ex) {
      return usage("Couldn't read grade book file \"" + gradeBookFile + "\"");

    } catch (ParserException ex) {
      return usage("Couldn't parse grade book file \"" + gradeBookFile + "\"");
    }
  }

  private static File getDirectory(String directoryName) {
    File directory = new File(directoryName);
    if (!directory.exists()) {
      return usage("Download directory \"" + directoryName + "\" does not exist");
    }

    if (!directory.isDirectory()) {
      return usage("Download directory \"" + directoryName + "\" is not a directory");
    }
    return directory;
  }

  private static String readGraderEmailPasswordFromFile(String passwordFile) {
    File file = new File(passwordFile);
    if (!file.exists()) {
      return usage("Password file \"" + passwordFile + "\" does not exist");
    }

    try {
      BufferedReader br = new BufferedReader(new FileReader(file));
      return br.readLine();

    } catch (IOException e) {
      return usage("Exception while reading password file");
    }
  }

  private static <T> T usage(String message) {
    PrintStream err = System.err;
    err.println("** " + message);
    err.println();
    err.println("FetchAndProcessGraderEmail passwordFile directory gradeBookFile whatToFetch");
    err.println("  passwordFile    File containing the password for the Grader's email account");
    err.println("  directory       The directory in which to download attachments");
    err.println("  gradeBookFile   Grade Book XML file related to this submission");
    err.println("  whatToFetch     What kind of student emails should be fetched");
    err.println("      projects    Project submissions");
    err.println("      surveys     Student surveys");
    err.println();

    System.exit(1);

    throw new IllegalStateException("Shouldn't reach here");
  }
}
