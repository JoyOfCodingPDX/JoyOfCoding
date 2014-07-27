package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;

import java.io.*;
import java.util.Optional;

public class GradesFromD2LImporter {

  public static void importGradesFromD2L(GradesFromD2L d2lGrades, GradeBook gradebook) {
    for (GradesFromD2L.D2LStudent d2LStudent : d2lGrades.getStudents()) {
      Student student = d2lGrades.findStudentInGradebookForD2LStudent(d2LStudent, gradebook)
        .orElseThrow(() -> new IllegalStateException("Could not find student " + d2LStudent + " in gradebook"));
      for (String quizName : d2LStudent.getQuizNames()) {
        Optional<Assignment> optional = d2lGrades.findAssignmentInGradebookForD2lQuiz(quizName, gradebook);
        Assignment quiz = optional.orElseThrow(() -> new IllegalStateException("No quiz named \"" + quizName + "\" in gradebook"));
        student.setGrade(quiz.getName(), new Grade(quiz, d2LStudent.getScore(quizName)));
      }
    }
  }

  public static void main(String[] args) throws IOException, ParserException {
    String d2lCsvFileName = null;
    String gradeBookFileName = null;

    for (String arg : args) {
      if (d2lCsvFileName == null) {
        d2lCsvFileName = arg;

      } else if (gradeBookFileName == null) {
        gradeBookFileName = arg;

      } else {
        usage("Extraneous command line argument: " + arg);
      }
    }

    if (d2lCsvFileName == null) {
      usage("Missing D2L CSV file name");
    }

    if (gradeBookFileName == null) {
      usage("Missing grade book file name");
    }

    GradesFromD2L d2lGrades = parseD2lCsvFile(d2lCsvFileName);
    GradeBook gradebook = parseGradeBook(gradeBookFileName);
    importGradesFromD2L(d2lGrades, gradebook);

    saveGradeBookIfModified(gradebook, gradeBookFileName);
  }

  private static void saveGradeBookIfModified(GradeBook gradeBook, String gradeBookFileName) {
    if (gradeBook.isDirty()) {
      File file = new File(gradeBookFileName);
      try {
        XmlDumper dumper = new XmlDumper(file);
        dumper.dump(gradeBook);

      } catch (IOException e) {
        usage("Can't write grade book in \"" + gradeBookFileName + "\"");
      }
    }
  }

  private static GradeBook parseGradeBook(String gradeBookFileName) throws IOException, ParserException {
    File gradeBookFile = new File(gradeBookFileName);
    if (!gradeBookFile.exists()) {
      usage("Grade Book file \"" + gradeBookFile + "\" does not exist");
    }

    XmlGradeBookParser parser = new XmlGradeBookParser(gradeBookFile);
    return parser.parse();
  }

  private static GradesFromD2L parseD2lCsvFile(String d2lCsvFileName) throws IOException {
    File d2lCsvFile = new File(d2lCsvFileName);
    if (!d2lCsvFile.exists()) {
      usage("D2L CSV file \"" + d2lCsvFile + "\" does not exist");
    }

    D2LCSVParser parser = new D2LCSVParser(new FileReader(d2lCsvFile));
    return parser.getGrades();
  }

  private static void usage(String message) {
    PrintStream err = System.err;

    err.println("+++ " + message);
    err.println();
    err.println("usage: java GradesFromD2LImporter d2lCsvFileName gradeBookFileName");
    err.println("    d2lCsvFileName       Name of the CSV file exported from D2L");
    err.println("    gradeBookFileName    Gradebook file");
    err.println();
    err.println("Imports grades from D2L into a gradebook");
    err.println();

    System.exit(1);
  }
}
