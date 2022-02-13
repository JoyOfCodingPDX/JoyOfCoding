package edu.pdx.cs410J.grader.canvas;

import edu.pdx.cs410J.grader.*;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GradesFromCanvasImporterTest {

  @Test
  public void errorWhenD2LQuizDoesNotExistInGradebook() {
    String studentId = "studentId";

    GradeBook gradebook = new GradeBook("test");
    Student student = new Student(studentId);
    gradebook.addStudent(student);

    GradesFromCanvas d2l = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2LStudent = GradesFromCanvas.newStudent().setFirstName("first").setLastName("last").setLoginId(studentId).create();
    d2LStudent.setScore("quiz", 3.4);
    d2l.addStudent(d2LStudent);

    assertThrows(IllegalStateException.class, () ->
      GradesFromCanvasImporter.importGradesFromCanvas(d2l, gradebook)
    );
  }

  @Test
  public void importScoreFromD2LWhenStudentIdMatchesD2LId() {
    String studentId = "studentId";
    String quizName = "quiz";
    double score = 3.4;

    GradeBook gradebook = new GradeBook("test");
    gradebook.addAssignment(new Assignment(quizName, 4.0));
    Student student = new Student(studentId);
    gradebook.addStudent(student);

    GradesFromCanvas d2l = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2LStudent = GradesFromCanvas.newStudent().setFirstName("first").setLastName("last").setLoginId(studentId).create();
    d2LStudent.setScore(quizName, score);
    d2l.addStudent(d2LStudent);

    GradesFromCanvasImporter.importGradesFromCanvas(d2l, gradebook);

    assertThat(student.getGradeNames(), hasItem(quizName));
    assertThat(student.getGrade(quizName).getScore(), equalTo(score));
  }

  @Test
  public void importScoreFromD2LWhenStudentNameMatchesD2LName() {
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

    GradesFromCanvas d2l = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2LStudent = GradesFromCanvas.newStudent().setFirstName(firstName).setLastName(lastName).setLoginId("d2LstudentId").create();
    d2LStudent.setScore(quizName, score);
    d2l.addStudent(d2LStudent);

    GradesFromCanvasImporter.importGradesFromCanvas(d2l, gradebook);

    assertThat(student.getGradeNames(), hasItem(quizName));
    assertThat(student.getGrade(quizName).getScore(), equalTo(score));
  }



}
