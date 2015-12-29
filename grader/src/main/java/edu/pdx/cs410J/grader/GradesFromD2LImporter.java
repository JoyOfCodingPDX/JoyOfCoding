package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Optional;

public class GradesFromD2LImporter {

  private static final Logger logger = LoggerFactory.getLogger("edu.pdx.cs410J.grader");

  public static void importGradesFromD2L(GradesFromD2L d2lGrades, GradeBook gradebook) {
    List<GradesFromD2L.D2LStudent> students = d2lGrades.getStudents();
    logger.debug("Importing grades for " + students.size() + " students");

    for (GradesFromD2L.D2LStudent d2LStudent : students) {
      Optional<Student> student = d2lGrades.findStudentInGradebookForD2LStudent(d2LStudent, gradebook);
      if (!student.isPresent()) {
        String message = "Could not find student " + d2LStudent + " in gradebook";
        System.err.println(message);

      } else {
        for (String quizName : d2LStudent.getQuizNames()) {
          Optional<Assignment> optional = d2lGrades.findAssignmentInGradebookForD2lQuiz(quizName, gradebook);
          Assignment quiz = optional.orElseThrow(() -> new IllegalStateException("No quiz named \"" + quizName + "\" in gradebook"));

          Double score = d2LStudent.getScore(quizName);
          logger.debug("Recording grade of " + score + " for " + student.get() + " for " + quiz.getName());
          student.get().setGrade(quiz.getName(), new Grade(quiz, score));
        }
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

    D2LGradesCSVParser parser = new D2LGradesCSVParser(new FileReader(d2lCsvFile));
    return parser.getGrades();
  }

  private static void usage(String message) {
    PrintStream err = System.err;

    err.println("+++ " + message);
    err.println();
    err.println("usage: java GradesFromD2LImporter d2lGradesCsvFileName gradeBookFileName");
    err.println("    d2lGradesCsvFileName       Name of the CSV grades file exported from D2L");
    err.println("    gradeBookFileName    Gradebook file");
    err.println();
    err.println("Imports grades from D2L into a gradebook");
    err.println();

    System.exit(1);
  }
}
