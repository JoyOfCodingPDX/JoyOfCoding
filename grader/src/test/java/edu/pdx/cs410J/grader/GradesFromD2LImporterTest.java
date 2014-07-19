package edu.pdx.cs410J.grader;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

public class GradesFromD2LImporterTest {

  @Test(expected = IllegalStateException.class)
  public void errorWhenD2LQuizDoesNotExistInGradebook() {
    String studentId = "studentId";

    GradeBook gradebook = new GradeBook("test");
    Student student = new Student(studentId);
    gradebook.addStudent(student);

    GradesFromD2L d2l = new GradesFromD2L();
    GradesFromD2L.D2LStudent d2LStudent = GradesFromD2L.newStudent().setFirstName("first").setLastName("last").setD2LId(studentId).create();
    d2LStudent.setScore("quiz", 3.4);
    d2l.addStudent(d2LStudent);

    GradesFromD2LImporter.importGradesFromD2L(d2l, gradebook);
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

    GradesFromD2L d2l = new GradesFromD2L();
    GradesFromD2L.D2LStudent d2LStudent = GradesFromD2L.newStudent().setFirstName("first").setLastName("last").setD2LId(studentId).create();
    d2LStudent.setScore(quizName, score);
    d2l.addStudent(d2LStudent);

    GradesFromD2LImporter.importGradesFromD2L(d2l, gradebook);

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

    GradesFromD2L d2l = new GradesFromD2L();
    GradesFromD2L.D2LStudent d2LStudent = GradesFromD2L.newStudent().setFirstName(firstName).setLastName(lastName).setD2LId("d2LstudentId").create();
    d2LStudent.setScore(quizName, score);
    d2l.addStudent(d2LStudent);

    GradesFromD2LImporter.importGradesFromD2L(d2l, gradebook);

    assertThat(student.getGradeNames(), hasItem(quizName));
    assertThat(student.getGrade(quizName).getScore(), equalTo(score));
  }



}
