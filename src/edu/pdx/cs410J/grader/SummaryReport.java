package edu.pdx.cs410J.grader;

import java.io.*;
import java.text.*;
import java.util.*;

import edu.pdx.cs410J.ParserException;

/**
 * Class that creates a pretty report that summarizes a student's
 * grades.
 */
public class SummaryReport {
  private static HashMap allTotals = new HashMap();  // Students -> Double

  /**
   * Computes the student's final average and makes a pretty report.
   */
  private static void dumpReportTo(GradeBook book, Student student,
                                   PrintWriter pw) {
    NumberFormat format = NumberFormat.getNumberInstance();
    format.setMinimumFractionDigits(1);
    format.setMaximumFractionDigits(1);

    Set grades = new HashSet();
    Assignment lowestQuiz = null;
    double best = 0.0;
    double total = 0.0;

    pw.println("Grade summary for: " + student.getFullName());
    SimpleDateFormat df = 
      new SimpleDateFormat("EEEE MMMM d, yyyy 'at' h:mm a");
    pw.println("Generated on: " + df.format(new Date()));
    pw.println("");

    Iterator allGradeNames = 
      (new TreeSet(book.getAssignmentNames())).iterator();
    while(allGradeNames.hasNext()) {
      String gradeName = (String) allGradeNames.next();
      Grade grade = student.getGrade(gradeName);
      Assignment assign = book.getAssignment(gradeName);

      // Average non-existent scores as zero
      double score;
      if(grade == null) {
        score = 0.0;

      } else {
        score = grade.getScore();
      }

      if(assign.getType() == Assignment.QUIZ) {
        if(lowestQuiz == null) {
          lowestQuiz = assign;

        } else {
          Grade lowestGrade = student.getGrade(lowestQuiz.getName());
          if(lowestGrade == null || score < lowestGrade.getScore()) {
            lowestQuiz = assign;
          }
        }
      }

      String line = "  " + assign.getName() + " (" +
        assign.getDescription() + "): " + format.format(score) + "/" +
        format.format(assign.getPoints());


      // Skip incompletes and no grades
      if(grade == null) {
        line += " (MISSING GRADE)";

      } else if(grade.getScore() == Grade.INCOMPLETE || 
                grade.getScore() == Grade.NOGRADE) {
        line += " (INCOMPLETE)";
      }

      pw.println(line);

      best += assign.getPoints();
      total += score;
    }

    if(lowestQuiz != null) {
      pw.println("");
      pw.println("Lowest Quiz grade dropped: " +
		 lowestQuiz.getName());
      pw.println("");

      // Subtract lowest quiz grade
      Grade lowestGrade = student.getGrade(lowestQuiz.getName());
      total -= (lowestGrade != null ? lowestGrade.getScore() : 0);
      best -= lowestQuiz.getPoints();
    }

    // Print out late and resubmitted assignments
    pw.println("Late assignments:");
    Iterator late = student.getLate().iterator();
    while(late.hasNext()) {
      pw.println("  " + late.next());
    }
    pw.println();

    pw.println("Resubmitted assignments");
    Iterator resubmit = student.getResubmitted().iterator();
    while(resubmit.hasNext()) {
      pw.println("  " + resubmit.next());
    }
    pw.println("");

    pw.println("Total grade: " + format.format(total)  + "/" +
               format.format(best));

    allTotals.put(student, new Double(total/best));
  }

  private static PrintWriter out = new PrintWriter(System.out, true);
  private static PrintWriter err = new PrintWriter(System.err, true);

  /**
   * Prints usage information about this main program
   */
  private static void usage() {
    err.println("\njava SummaryReport xmlFile outDir");
    err.println("\n");
    System.exit(1);
  }

  /**
   * Main program that creates summary reports for every student in a
   * grade book located in a given XML file.
   */
  public static void main(String[] args) {
    if(args.length < 2) {
      err.println("** Not enough arguments");
      usage();
    }

    String xmlFile = args[0];
    File outDir = new File(args[1]);
    if(!outDir.exists()) {
      outDir.mkdirs();

    } else if(!outDir.isDirectory()) {
      err.println("** " + outDir + " is not a directory");
      System.exit(1);
    }

    File file = new File(xmlFile);
    if(!file.exists()) {
      err.println("** Grade book file " + xmlFile + 
                  " does not exist");
      System.exit(1);
    }

    // Parse XML file
    GradeBook book = null;
    try {
      err.println("Parsing " + file);
      XmlParser parser = new XmlParser(file);
      book = parser.parse();

    } catch(FileNotFoundException ex) {
      err.println("** Could not find file: " + ex.getMessage());
      System.exit(1);

    } catch(IOException ex) {
      err.println("** IOException during parsing: " + ex.getMessage());
      System.exit(1);

    } catch(ParserException ex) {
      err.println("** Exception while parsing " + file + ": " + ex);
      System.exit(1);
    }

    // Create a SummaryReport for every student
    Iterator ids = book.getStudentIds().iterator();
    while(ids.hasNext()) {
      String id = (String) ids.next();

      err.println(id);

      Student student = book.getStudent(id);
      
      File outFile = new File(outDir, id + ".report");
      try {
        PrintWriter pw = 
          new PrintWriter(new FileWriter(outFile), true);
        dumpReportTo(book, student, pw);

//         dumpReportTo(book, student, out);
      } catch(IOException ex) {
        ex.printStackTrace();
        System.exit(1);
      }
    }

    // Sort students by totals and print out results:
    TreeSet sorted = new TreeSet(new Comparator() {
        public int compare(Object o1, Object o2) {
          Student s1 = (Student) o1;
          Student s2 = (Student) o2;
          Double d1 = (Double) allTotals.get(s1);
          Double d2 = (Double) allTotals.get(s2);
          return(d2.compareTo(d1));
        }

        public boolean equals(Object o) {
          return(true);
        }
      });

    sorted.addAll(allTotals.keySet());

    NumberFormat format = NumberFormat.getPercentInstance();


    Iterator iter = sorted.iterator();
    while(iter.hasNext()) {
      Student student = (Student) iter.next();
      Double d = (Double) allTotals.get(student);
      out.println(student + ": " + format.format(d.doubleValue()));
    }
  }

}
