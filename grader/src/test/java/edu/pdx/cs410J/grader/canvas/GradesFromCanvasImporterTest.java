package edu.pdx.cs410J.grader.canvas;

import edu.pdx.cs410J.grader.*;
import edu.pdx.cs410J.grader.canvas.GradesFromCanvas.CanvasAssignment;
import edu.pdx.cs410J.grader.canvas.GradesFromCanvas.CanvasStudent;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GradesFromCanvasImporterTest extends CanvasTestCase {

  @Test
  public void errorWhenCanvasQuizDoesNotExistInGradebook() {
    String studentId = "studentId";

    GradeBook gradebook = new GradeBook("test");
    Student student = new Student(studentId);
    gradebook.addStudent(student);

    GradesFromCanvas canvas = new GradesFromCanvas();
    CanvasAssignment assignment = new CanvasAssignment("quiz", 1234);
    CanvasStudent canvasStudent = createCanvasStudentWithId(studentId);
    canvasStudent.setScore(assignment, 3.4);
    canvas.addStudent(canvasStudent);

    assertThrows(IllegalStateException.class, () ->
      GradesFromCanvasImporter.importGradesFromCanvas(canvas, gradebook)
    );
  }

  private CanvasStudent createCanvasStudentWithId(String studentId) {
    return createCanvasStudent("First", "Last", studentId);
  }

  @Test
  public void importScoreFromCanvasWhenStudentIdMatchesCanvasId() {
    String studentId = "studentId";
    String quizName = "quiz";
    double score = 3.4;

    GradeBook gradebook = new GradeBook("test");
    gradebook.addAssignment(new Assignment(quizName, 4.0));
    Student student = new Student(studentId);
    gradebook.addStudent(student);

    GradesFromCanvas canvas = new GradesFromCanvas();
    CanvasAssignment assignment = new CanvasAssignment(quizName, 1234);
    CanvasStudent canvasStudent = createCanvasStudentWithId(studentId);
    canvasStudent.setScore(assignment, score);
    canvas.addStudent(canvasStudent);

    GradesFromCanvasImporter.importGradesFromCanvas(canvas, gradebook);

    assertThat(student.getGradeNames(), hasItem(quizName));
    assertThat(student.getGrade(quizName).getScore(), equalTo(score));
  }

  @Test
  public void importScoreFromCanvasWhenStudentNameMatchesCanvasName() {
    String firstName = "first";
    String lastName = "last";

    String quizName = "quiz";
    double score = 3.4;

    GradeBook gradebook = new GradeBook("test");
    gradebook.addAssignment(new Assignment(quizName, 4.0));
    Student student = new Student("studentId");
    student.setFirstName(firstName);
    student.setLastName(lastName);
    gradebook.addStudent(student);

    GradesFromCanvas canvas = new GradesFromCanvas();
    CanvasStudent canvasStudent = createCanvasStudent(firstName, lastName);
    CanvasAssignment quiz = new CanvasAssignment(quizName, 1234);
    canvasStudent.setScore(quiz, score);
    canvas.addStudent(canvasStudent);

    GradesFromCanvasImporter.importGradesFromCanvas(canvas, gradebook);

    assertThat(student.getGradeNames(), hasItem(quizName));
    assertThat(student.getGrade(quizName).getScore(), equalTo(score));
  }



}
