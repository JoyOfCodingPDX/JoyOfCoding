package edu.pdx.cs.joy.grader.canvas;

import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.grader.gradebook.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

public class GradesFromCanvasImporter {

  private static final Logger logger = LoggerFactory.getLogger("edu.pdx.cs410J.grader");

  public static void importGradesFromCanvas(GradesFromCanvas canvasGrades, GradeBook gradebook) {
    List<GradesFromCanvas.CanvasStudent> students = canvasGrades.getStudents();
    logger.debug("Importing grades for " + students.size() + " students");

    for (GradesFromCanvas.CanvasStudent canvasStudent : students) {
      Optional<Student> student = canvasGrades.findStudentInGradebookForCanvasStudent(canvasStudent, gradebook);
      if (student.isEmpty()) {
        String message = "Could not find student " + canvasStudent + " in gradebook";
        System.err.println(message);

      } else {
        for (GradesFromCanvas.CanvasAssignment canvasAssignment : canvasStudent.getAssignments()) {
          Optional<Assignment> optional = canvasGrades.findAssignmentInGradebookForCanvasQuiz(canvasAssignment, gradebook);
          Assignment assignment = optional.orElseThrow(() -> new IllegalStateException("No assignment named \"" + canvasAssignment.getName() + "\" in gradebook"));

          Double score = canvasStudent.getScore(canvasAssignment);
          logger.debug("Recording grade of " + score + " for " + student.get() + " for " + assignment.getName());
          student.get().setGrade(assignment.getName(), new Grade(assignment, score));
        }
      }
    }
  }

  public static void main(String[] args) throws IOException, ParserException {
    String canvasCsvFileName = null;
    String gradeBookFileName = null;

    for (String arg : args) {
      if (canvasCsvFileName == null) {
        canvasCsvFileName = arg;

      } else if (gradeBookFileName == null) {
        gradeBookFileName = arg;

      } else {
        usage("Extraneous command line argument: " + arg);
      }
    }

    if (canvasCsvFileName == null) {
      usage("Missing Canvas CSV file name");
    }

    if (gradeBookFileName == null) {
      usage("Missing grade book file name");
    }

    GradesFromCanvas canvasGrades = parseCanvasCsvFile(canvasCsvFileName);
    GradeBook gradebook = parseGradeBook(gradeBookFileName);
    importGradesFromCanvas(canvasGrades, gradebook);

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

  private static GradesFromCanvas parseCanvasCsvFile(String canvasCsvFileName) throws IOException {
    File canvasCsvFile = new File(canvasCsvFileName);
    if (!canvasCsvFile.exists()) {
      usage("Canvas CSV file \"" + canvasCsvFile + "\" does not exist");
    }

    CanvasGradesCSVParser parser = new CanvasGradesCSVParser(new FileReader(canvasCsvFile));
    return parser.getGrades();
  }

  private static void usage(String message) {
    PrintStream err = System.err;

    err.println("+++ " + message);
    err.println();
    err.println("usage: java GradesFromCanvasImporter canvasGradesCsvFileName gradeBookFileName");
    err.println("    canvasGradesCsvFileName       Name of the CSV grades file exported from Canvas");
    err.println("    gradeBookFileName             Gradebook file");
    err.println();
    err.println("Imports grades from Canvas into a gradebook");
    err.println();

    System.exit(1);
  }
}
