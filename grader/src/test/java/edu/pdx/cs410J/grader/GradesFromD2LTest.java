package edu.pdx.cs410J.grader;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class GradesFromD2LTest {

  @Test
  public void noStudentInGradebookThatMatchesD2LStudent() {
    GradesFromD2L grades = new GradesFromD2L();
    GradesFromD2L.D2LStudent d2lStudent = GradesFromD2L.newStudent().setFirstName("firstName").setLastName("lastName").setD2LId("d2lId").create();
    grades.addStudent(d2lStudent);

    GradeBook book = new GradeBook("test");
    Student student = new Student("studentId");
    student.setD2LId("notD2LId");
    book.addStudent(student);

    assertThat(grades.findStudentInGradebookForD2LStudent(d2lStudent, book), nullValue());
  }

  @Test
  public void matchStudentByD2LId() {
    GradesFromD2L grades = new GradesFromD2L();
    GradesFromD2L.D2LStudent d2lStudent = GradesFromD2L.newStudent().setFirstName("firstName").setLastName("lastName").setD2LId("d2lId").create();
    grades.addStudent(d2lStudent);

    GradeBook book = new GradeBook("test");
    Student student = new Student("studentId");
    student.setD2LId("d2lId");
    book.addStudent(student);

    assertThat(grades.findStudentInGradebookForD2LStudent(d2lStudent, book), equalTo(student));
  }

}
