package edu.pdx.cs410J.grader;

import java.io.*;
import java.util.*;

/**
 * Quicky program that parses a text file containing student's grades.
 */
public class ParseTextFile {
  private static PrintWriter err = new PrintWriter(System.err, true);

  /**
   * Prints usage information for this program
   */
  private static void usage() {
    err.println("\njava ParseTextFile textFile courseName xmlFile");
    err.println("\n");
    System.exit(1);
  }

  public static void main(String[] args) {
    if (args.length < 3) {
      err.println("** Missing command line arguments");
      usage();
    }
    
    String textFile = args[0];
    String courseName = args[1];
    String xmlFile = args[2];

    // Make a grade book
    GradeBook book = new GradeBook(courseName);

    // Parse the text file
    BufferedReader in = null;
    try {
      in = new BufferedReader(new FileReader(textFile));

      List<String> assignments = new ArrayList<String>();

      // First line of file contains names of assignments
      String line = in.readLine();
      StringTokenizer st = new StringTokenizer(line);
      st.nextToken();    // Skip "id"
      while (st.hasMoreTokens()) {
        String assignment = st.nextToken();
        assignments.add(assignment);
      }

      line = in.readLine();
      while (line != null) {
        st = new StringTokenizer(line);
        String studentName = st.nextToken();
        Student student =
          book.getStudent(studentName).orElseThrow(() -> new IllegalStateException("No student with id " + studentName));

        for (int i = 0; st.hasMoreTokens(); i++) {
          String s = st.nextToken();
          String assignment = (String) assignments.get(i);
          double score;

          if (s.equals("I")) {
            score = Grade.INCOMPLETE;

          } else {
            try {
              score = Double.parseDouble(s);
            } catch (NumberFormatException ex) {
              err.println("** Bad number: " + s);
              System.exit(1);
              score = -4.2;
            }
          }

          Grade grade = new Grade(assignment, score);
          student.setGrade(assignment, grade);
        }

        line = in.readLine();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
      System.exit(1);
    }

    // Save grade book to XML file
    try {
      XmlDumper dumper = new XmlDumper(xmlFile);
      dumper.dump(book);

    } catch (IOException ex) {
      err.println("** Error while writing XML file: " + ex);
      System.exit(1);
    }
  }
}
