package edu.pdx.cs410J.grader;

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs410J.ParserException;

import java.io.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static edu.pdx.cs410J.grader.Student.Section.*;

/**
 * Class that creates a pretty report that summarizes a student's
 * grades.
 */
public class SummaryReport {
  static final String UNDERGRADUATE_DIRECTORY_NAME = "undergraduates";
  static final String GRADUATE_DIRECTORY_NAME = "graduates";
  private static HashMap<Student, Double> allTotals = new HashMap<>();

  /**
   * Computes the student's final average and makes a pretty report.
   */
  @VisibleForTesting
  static void dumpReportTo(GradeBook book, Student student,
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

      if (assignment.getType() == Assignment.AssignmentType.QUIZ) {
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
      if (assignment.getType() == Assignment.AssignmentType.OPTIONAL) {
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
      if (assignment.getType() != Assignment.AssignmentType.OPTIONAL) {
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
      LetterGrade letterGrade = book.getLetterGradeForScore(student.getEnrolledSection(), overallScore * 100.0);
      student.setLetterGrade(letterGrade);
    }

    if (student.getLetterGrade() != null) {
      pw.println("Letter Grade: " + student.getLetterGrade());
    }

    allTotals.put(student, overallScore);
  }

  static boolean noStudentHasGradeFor(Assignment assignment, GradeBook book) {
    return book.studentsStream().noneMatch(student -> studentHasGradeFor(student, assignment));
  }

  private static boolean studentHasGradeFor(Student student, Assignment assignment) {
    Grade grade = student.getGrade(assignment.getName());
    return grade != null && !grade.isNotGraded();
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
    return new TreeSet<>(book.getAssignmentNames());
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
    Collection<String> students = new ArrayList<>();

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
      return;
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

    GradeBook book = parseGradeBook(file);

    // Create a SummaryReport for every student
    Iterable<String> studentIds;

    if (!students.isEmpty()) {
      studentIds = students;

    } else {
      studentIds = book.getStudentIds();
    }

    dumpReports(studentIds, book, outDir, assignLetterGrades);

    // Sort students by totals and print out results:
    Set<Student> students1 = allTotals.keySet();
    printOutStudentTotals(students1, out);

    saveGradeBookIfDirty(xmlFileName, book);

  }

  @VisibleForTesting
  static void printOutStudentTotals(Set<Student> allStudents, PrintWriter out) {
    SortedSet<Student> sorted = getStudentSortedByTotalPoints(allStudents);

    out.println("Undergraduates:");
    Stream<Student> undergrads = sorted.stream().filter(student -> student.getEnrolledSection() == UNDERGRADUATE);
    printOutStudentTotals(out, undergrads);

    out.println("Graduate Students:");
    Stream<Student> grads = sorted.stream().filter(student -> student.getEnrolledSection() == GRADUATE);
    printOutStudentTotals(out, grads);

  }

  private static void printOutStudentTotals(PrintWriter out, Stream<Student> students) {
    NumberFormat format = NumberFormat.getPercentInstance();

    students.forEach(student -> {
      Double d = allTotals.get(student);
      out.print("  " + student + ": " + format.format(d.doubleValue()));

      if (student.getLetterGrade() != null) {
        out.print(" " + student.getLetterGrade());
      }

      out.println();
    });
  }

  private static void saveGradeBookIfDirty(String xmlFileName, GradeBook book) {
    if (book.isDirty()) {
      try {
        XmlDumper dumper = new XmlDumper(xmlFileName);
        dumper.dump(book);

      } catch (IOException ex) {
        printErrorMessageAndExit("While saving gradebook to " + xmlFileName, ex);
      }
    }
  }

  private static SortedSet<Student> getStudentSortedByTotalPoints(Set<Student> students) {
    SortedSet<Student> sorted = new TreeSet<>(new Comparator<Student>() {
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

    sorted.addAll(students);
    return sorted;
  }

  @VisibleForTesting
  static void dumpReports(Iterable<String> studentIds, GradeBook book, File outDir, boolean assignLetterGrades) {
    for (String id : studentIds) {
      err.println(id);

      Student student = book.getStudent(id).orElseThrow(noStudentWithId(id));

      File outFile = new File(getDirectoryForReportFileForStudent(outDir, student), getReportFileName(id));
      try {
        PrintWriter pw =
          new PrintWriter(new FileWriter(outFile), true);
        dumpReportTo(book, student, pw, assignLetterGrades);

//         dumpReportTo(book, student, out);
      } catch (IOException ex) {
        printErrorMessageAndExit("While writing report to " + outFile, ex);
      }
    }
  }

  private static File getDirectoryForReportFileForStudent(File parentDirectory, Student student) {
    String directoryName;
    Student.Section enrolledSection = student.getEnrolledSection();
    switch (enrolledSection) {
      case UNDERGRADUATE:
        directoryName = UNDERGRADUATE_DIRECTORY_NAME;
        break;
      case GRADUATE:
        directoryName = GRADUATE_DIRECTORY_NAME;
        break;
      default:
        throw new IllegalStateException("Don't know directory name for " + enrolledSection);
    }
    File directory = new File(parentDirectory, directoryName);
    directory.mkdirs();
    return directory;
  }

  @VisibleForTesting
  static String getReportFileName(String studentId) {
    return studentId + ".report";
  }

  private static GradeBook parseGradeBook(File gradeBookFile) {
    GradeBook book = null;
    try {
      err.println("Parsing " + gradeBookFile);
      XmlGradeBookParser parser = new XmlGradeBookParser(gradeBookFile);
      book = parser.parse();

    } catch (FileNotFoundException ex) {
      printErrorMessageAndExit("** Could not find grade book file: " + gradeBookFile, ex);

    } catch (ParserException | IOException ex) {
      printErrorMessageAndExit("** Exception while parsing " + gradeBookFile, ex);
    }
    return book;
  }

  private static void printErrorMessageAndExit(String message, Throwable ex) {
    err.println(message);
    System.exit(1);
  }

  @SuppressWarnings("ThrowableInstanceNeverThrown")
  private static Supplier<? extends IllegalStateException> noStudentWithId(String id) {
    return () -> new IllegalStateException("No student with id " + id);
  }

}
