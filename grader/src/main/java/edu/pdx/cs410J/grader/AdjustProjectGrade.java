package edu.pdx.cs410J.grader;

import java.io.*;

/**
 * This program subtracts some number of points off of each student's
 * grade for a given project.  It is used when I forget to take the
 * POA into account when grading.
 */
public class AdjustProjectGrade {
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

    for (String arg : args) {
      if (fileName == null) {
        fileName = arg;

      } else if (proj == null) {
        proj = arg;

      } else if (pointsString == null) {
        pointsString = arg;

      } else {
        usage("Spurious command line: " + arg);
      }
    }

    if (fileName == null) {
      usage("Missing file name");
      return;
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
      return;
    }

    if (assign.getType() != Assignment.AssignmentType.PROJECT) {
      usage("Assignment \"" + proj + "\" is not a project");
    }

    adjustGradeForEachStudent(book, proj, points);

    XmlDumper dumper = new XmlDumper(file);
    dumper.dump(book);
  }

  private static void adjustGradeForEachStudent(GradeBook book, String projectName, double adjustment) {
    book.forEachStudent((Student student) -> {
      Grade grade = student.getGrade(projectName);
      if (grade != null) {
        grade.setScore(grade.getScore() - adjustment);
      }
    });
  }

}
