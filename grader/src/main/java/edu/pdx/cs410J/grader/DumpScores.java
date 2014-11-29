package edu.pdx.cs410J.grader;

import java.io.*;
import java.util.*;

/**
 * This program sorts the scores for a given assignment and dumps them
 * to standard out.
 */
public class DumpScores {

  /**
   * Inner class that represents a score/Student tuple
   */
  private static class Tuple implements Comparable {
    private double score;
    private Student student;

    Tuple(Student student, double score) {
      this.student = student;
      this.score = score;
    }

    public int compareTo(Object o) {
      Tuple other = (Tuple) o;
      if (this.score == other.score) {
        return this.student.getId().compareTo(other.student.getId());

      } else if (this.score > other.score) {
        return -1;

      } else if (this.score < other.score) {
        return 1;

      } else {
//          assert this.score == other.score;
        return 0;
      }
    }

    public String toString() {
      return this.student + ": " + this.score;
    }
    
  }

  //////////////////////  Main Program  ///////////////////////

  private static PrintStream out = System.out;
  private static PrintStream err = System.err;

  /**
   * Prints usage information about this program
   */
  private static void usage(String s) {
    err.println("\n** " + s + "\n");
    err.println("usage: java DumpScores xmlFile assignment");
    err.println("");
    err.println("Dumps the grades for a given assignment to " +
                "standard out");
    err.println("");
    System.exit(1);
  }

  public static void main(String[] args) throws Throwable {
    String xmlFileName = null;
    String assignmentName = null;

    for (int i = 0; i < args.length; i++) {
      if (xmlFileName == null) {
        xmlFileName = args[i];

      } else if (assignmentName == null) {
        assignmentName = args[i];

      } else {
        usage("Extraneous command line: " + args[i]);
      }
    }

    if (xmlFileName == null) {
      usage("Missing XML file");

    } else if (assignmentName == null) {
      usage("Missing assignment");
    }

    File xmlFile = new File(xmlFileName);
    if (!xmlFile.exists()) {
      usage("File \"" + xmlFileName + "\" does not exist");
    }

    GradeBook book = (new XmlGradeBookParser(xmlFile)).parse();
    Assignment assign = book.getAssignment(assignmentName);
    if (assign == null) {
      usage("No such assignment \"" + assignmentName + "\"");
    }

    // Maps score to the student
    SortedSet<Tuple> scores = new TreeSet<Tuple>();

    book.forEachStudent(student -> {
      Grade grade = student.getGrade(assign.getName());
      if (grade != null) {
        double score = grade.getScore();
        scores.add(new Tuple(student, score));
      }
    });

    out.println("\nGrades for " + assign + ": " +
                assign.getDescription());

    Iterator iter = scores.iterator();
    while (iter.hasNext()) {
      out.println("  " + iter.next());
    }

    out.println("");
  }

}
