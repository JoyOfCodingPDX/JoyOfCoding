package edu.pdx.cs410J.grader;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class StudentFileMailer {

  public static void main(String[] args) {
    String gradeBookFileName = null;
    String subject = null;
    List<String> fileNames = new ArrayList<>();

    for (String arg : args) {
      if (gradeBookFileName == null) {
        gradeBookFileName = arg;

      } else if (subject == null) {
        subject = arg;

      } else {
        fileNames.add(arg);
      }
    }

    usageIfNull(gradeBookFileName, "Missing grade book file name");
    usageIfNull(subject, "Missing email subject");
    usageIfEmpty(fileNames, "Missing file names");
  }

  private static void usageIfEmpty(List<String> list, String message) {
    if (list.isEmpty()) {
      usage(message);
    }
  }

  private static void usageIfNull(String arg, String message) {
    if (arg == null) {
      usage(message);
    }
  }

  private static void usage(String message) {
    PrintStream err = System.err;

    err.println("** " + message);
    err.println();
    err.println("usage: java StudentFileMailer gradeBookFileName subject fileName+");
    err.println();
    err.println("Emails a file to a student in a grade book.  The name of the file must");
    err.println("begin with the student's id.");
    err.println();
    err.println("  gradeBookFileName    Name of the grade book file");
    err.println("  subject              The subject of the email");
    err.println("  fileName             A file to send to the student");
    err.println();

    System.exit(1);
  }
}
