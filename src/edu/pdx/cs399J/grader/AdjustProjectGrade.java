package edu.pdx.cs399J.grader;

import java.io.*;
import java.util.*;

/**
 * This program subtracts some number of points off of each student's
 * grade for a given project.  It is used when I forget to take the
 * POA into account when grading.
 */
public class AdjustProjectGrade {
  private static PrintStream out = System.out;
  private static PrintStream err = System.err;
  
  /**
   * Prints usage information about this program
   */
  private static void usage(String s) {
    err.println("\n** " + s + "\n");
    err.println("usage: java AdjustProjectGrade xmlFile proj points");
    err.println("  xmlFile  XML file containing the grade book");
    err.println("  proj     The project from which to deduct points");
    err.println("  points   The number of points to deduct");
    err.println("");
    err.println("This program deducts a number of points off of " +
                "each students grade for a given project.");
    err.println("");
    System.exit(1);
  }

  public static void main(String[] args) throws Throwable {
    String fileName = null;
    String proj = null;
    String pointsString = null;

    for (int i = 0; i < args.length; i++) {
      if (fileName == null) {
        fileName = args[i];

      } else if (proj == null) {
        proj = args[i];

      } else if (pointsString == null) {
        pointsString = args[i];

      } else {
        usage("Spurious command line: " + args[i]);
      }
    }

    if (fileName == null) {
      usage("Missing file name");
    }

    if (proj == null) {
      usage("Missing project");
    }

    if (pointsString == null) {
      usage("Missing points");
    }

    double points;
    try {
      points = Double.parseDouble(pointsString);

    } catch (NumberFormatException ex) {
      usage("Invalid points: " + pointsString);
      return;
    }

    File file = new File(fileName);
    if (!file.exists()) {
      usage("File \"" + file + "\" does not exist");
    }

    XmlGradeBookParser parser = new XmlGradeBookParser(file);
    GradeBook book = parser.parse();
    
    Assignment assign = book.getAssignment(proj);
    if (assign == null) {
      usage("No such assignment: " + proj);
    }

    if (assign.getType() != Assignment.PROJECT) {
      usage("Assignment \"" + proj + "\" is not a project");
    }

    Iterator ids = book.getStudentIds().iterator();
    while (ids.hasNext()) {
      String id = (String) ids.next();
      Student student = book.getStudent(id);
      Grade grade = student.getGrade(proj);
      if (grade != null) {
        grade.setScore(grade.getScore() - points);
      }
    }

    XmlDumper dumper = new XmlDumper(file);
    dumper.dump(book);
  }

}
