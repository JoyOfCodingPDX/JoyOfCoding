package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;

import java.io.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class that creates a pretty report that summarizes a student's
 * grades.
 */
public class SummaryReport {
  private static HashMap<Student, Double> allTotals = new HashMap<Student, Double>();

  /**
   * Computes the student's final average and makes a pretty report.
   */
  private static void dumpReportTo(GradeBook book, Student student,
                                   PrintWriter pw, boolean assignLetterGrades) {
    NumberFormat format = NumberFormat.getNumberInstance();
    format.setMinimumFractionDigits(1);
    format.setMaximumFractionDigits(1);

    Assignment lowestQuiz = null;
    double best = 0.0;
    double total = 0.0;

    pw.println("Grade summary for: " + student.getFullName());
    SimpleDateFormat df = 
      new SimpleDateFormat("EEEE MMMM d, yyyy 'at' h:mm a");
    pw.println("Generated on: " + df.format(new Date()));
    pw.println("");

    for (String assignmentName : getSortedAssignmentNames(book)) {
      Assignment assignment = book.getAssignment(assignmentName);
      if (noStudentHasGradeFor(assignment, book)) {
        continue;
      }

      Grade grade = student.getGrade(assignmentName);
      double score = getScore(grade);


//       System.out.println("Examining " + assign + ", score: " + score);

      if (assignment.getType() == Assignment.QUIZ) {
        if (lowestQuiz == null) {
          lowestQuiz = assignment;
//           System.out.println("Lowest quiz: " + lowestQuiz + 
//                              ", score: " + score);

        } else {
          Grade lowestGrade = student.getGrade(lowestQuiz.getName());
          if (lowestGrade != null && score < lowestGrade.getScore()) {
            lowestQuiz = assignment;
//             System.out.println("Lowest quiz: " + lowestQuiz + ", score: "
//                                + score + ", lowest grade: " +
//                                student.getGrade(lowestQuiz.getName()));
          }
        }
      }

      StringBuffer line = new StringBuffer();
      line.append("  ");
      line.append(assignment.getName());
      line.append(" (");
      line.append(assignment.getDescription());
      line.append(")");
      if (assignment.getType() == Assignment.OPTIONAL) {
        line.append(" (OPTIONAL)");
      }
      line.append(": ");
      line.append(format.format(score));
      line.append("/");
      line.append(format.format(assignment.getPoints()));


      // Skip incompletes and no grades
      if (grade == null) {
        line.append(" (MISSING GRADE)");

      } else if (grade.isIncomplete()) {
        line.append(" (INCOMPLETE)");

      } else if (grade.isNotGraded()) {
        line.append( "(NOT GRADED)");
      }

      pw.println(line);

      // Don't count optional assignments toward the maximum point
      // total
      if (assignment.getType() != Assignment.OPTIONAL) {
        best += assignment.getPoints();
      }

      total += score;
    }

    if (lowestQuiz != null) {
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
    for (String late : student.getLate()) {
      pw.println("  " + late);
    }
    pw.println();

    pw.println("Resubmitted assignments:");
    for (String resubmitted : student.getResubmitted()) {
      pw.println("  " + resubmitted);
    }
    pw.println("");

    pw.println("Total grade: " + format.format(total)  + "/" +
               format.format(best));

    double overallScore = total / best;

    if (assignLetterGrades) {
      LetterGrade letterGrade = book.getLetterGradeForScore(overallScore * 100.0);
      student.setLetterGrade(letterGrade);
    }

    if (student.getLetterGrade() != null) {
      pw.println("Letter Grade: " + student.getLetterGrade());
    }

    allTotals.put(student, overallScore);
  }

  static boolean noStudentHasGradeFor(Assignment assignment, GradeBook book) {
    for (String studentId : book.getStudentIds()) {
      Student student = book.getStudent(studentId);
      Grade grade = student.getGrade(assignment.getName());
      if (grade != null && !grade.isNotGraded()) {
        return false;
      }
    }

    return true;
  }

  private static double getScore(Grade grade) {
    // Average non-existent scores as zero
    double score;
    if (grade == null) {
      score = 0.0;

    } else {
      score = grade.getScore();
    }
    return score;
  }

  private static SortedSet<String> getSortedAssignmentNames(GradeBook book) {
    return new TreeSet<String>(book.getAssignmentNames());
  }

  private static PrintWriter out = new PrintWriter(System.out, true);
  private static PrintWriter err = new PrintWriter(System.err, true);

  /**
   * Prints usage information about this main program
   */
  private static void usage(String s) {
    err.println("\n** " + s + "\n");
    err.println("\njava SummaryReport -assignLetterGrades xmlFile outDir (student)*");
    err.println("\n");
    err.println("Generates summary grade reports for the given " +
		"students.  If student is not \ngiven, then reports " + 
		"for all students are generated.");
    err.println("");
    System.exit(1);
  }

  /**
   * Main program that creates summary reports for every student in a
   * grade book located in a given XML file.
   */
  public static void main(String[] args) {
    boolean assignLetterGrades = false;
    String xmlFileName = null;
    String outputDirName = null;
    Collection<String> students = new ArrayList<String>();

    for (String arg : args) {
      if (arg.equals("-assignLetterGrades")) {
        assignLetterGrades = true;

      } else if (xmlFileName == null) {
        xmlFileName = arg;

      } else if (outputDirName == null) {
        outputDirName = arg;

      } else {
        students.add(arg);
      }
    }

    if (xmlFileName == null) {
      usage("Missing XML file name");
    }

    if (outputDirName == null) {
      usage("Missing output dir name");
    }

    String xmlFile = xmlFileName;
    File outDir = new File(outputDirName);
    if (!outDir.exists()) {
      outDir.mkdirs();

    } else if (!outDir.isDirectory()) {
      usage(outDir + " is not a directory");
    }

    File file = new File(xmlFile);
    if (!file.exists()) {
      usage("Grade book file " + xmlFile + " does not exist");
    }

    // Parse XML file
    GradeBook book = null;
    try {
      err.println("Parsing " + file);
      XmlGradeBookParser parser = new XmlGradeBookParser(file);
      book = parser.parse();

    } catch (FileNotFoundException ex) {
      err.println("** Could not find file: " + ex.getMessage());
      System.exit(1);

    } catch (IOException ex) {
      err.println("** IOException during parsing: " + ex.getMessage());
      System.exit(1);

    } catch (ParserException ex) {
      err.println("** Exception while parsing " + file + ": " + ex);
      System.exit(1);
    }

    // Create a SummaryReport for every student
    Iterable<String> studentIds;

    if (!students.isEmpty()) {
      studentIds = students;

    } else {
      studentIds = book.getStudentIds();
    }

    for (String id : studentIds) {
      err.println(id);

      Student student = book.getStudent(id);
      
      File outFile = new File(outDir, id + ".report");
      try {
        PrintWriter pw = 
          new PrintWriter(new FileWriter(outFile), true);
        dumpReportTo(book, student, pw, assignLetterGrades);

//         dumpReportTo(book, student, out);
      } catch (IOException ex) {
        ex.printStackTrace();
        System.exit(1);
      }
    }

    // Sort students by totals and print out results:
    TreeSet<Student> sorted = new TreeSet<Student>(new Comparator<Student>() {
        @Override
        public int compare(Student s1, Student s2) {
          Double d1 = allTotals.get(s1);
          Double d2 = allTotals.get(s2);
          if ( d2.compareTo( d1 ) == 0 ) {
            return s1.getId().compareTo( s2.getId() );
          } else {
            return d2.compareTo(d1);
          }
        }

        @Override
        public boolean equals(Object o) {
          return true;
        }
      });

    sorted.addAll(allTotals.keySet());

    NumberFormat format = NumberFormat.getPercentInstance();

    for (Student student : sorted) {
      Double d = allTotals.get(student);
      out.print(student + ": " + format.format(d.doubleValue()));

      if (student.getLetterGrade() != null) {
        out.print(" " + student.getLetterGrade());
      }

      out.println();
    }

    if (book.isDirty()) {
      try {
        XmlDumper dumper = new XmlDumper(xmlFileName);
        dumper.dump(book);

      } catch (IOException ex) {
        ex.printStackTrace();
        System.exit(1);
      }
    }

  }

}
