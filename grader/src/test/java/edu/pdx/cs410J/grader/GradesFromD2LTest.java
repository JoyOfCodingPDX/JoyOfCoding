package edu.pdx.cs410J.grader;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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

    assertThat(grades.findStudentInGradebookForD2LStudent(d2lStudent, book), is(nullValue()));
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

  @Test
  public void matchStudentByFirstAndLastName() {
    String d2lId = "d2lId";
    String firstName = "firstName";
    String lastName = "lastName";

    GradesFromD2L grades = new GradesFromD2L();
    GradesFromD2L.D2LStudent d2lStudent = GradesFromD2L.newStudent().setFirstName(firstName).setLastName(lastName).setD2LId(d2lId).create();
    grades.addStudent(d2lStudent);

    GradeBook book = new GradeBook("test");
    Student student = new Student("studentId");
    student.setFirstName(firstName);
    student.setLastName(lastName);
    book.addStudent(student);
    book.makeClean();

    assertThat(student.getD2LId(), is(nullValue()));
    assertThat(student.isDirty(), is(false));

    assertThat(grades.findStudentInGradebookForD2LStudent(d2lStudent, book), equalTo(student));
    assertThat(student.getD2LId(), equalTo(d2lId));
    assertThat(student.isDirty(), is(true));
  }

  @Test
  public void matchStudentByFirstAndLastNameIgnoringCase() {
    String d2lId = "d2lId";
    String firstName = "firstName";
    String lastName = "lastName";

    GradesFromD2L grades = new GradesFromD2L();
    GradesFromD2L.D2LStudent d2lStudent = GradesFromD2L.newStudent().setFirstName(firstName).setLastName(lastName).setD2LId(d2lId).create();
    grades.addStudent(d2lStudent);

    GradeBook book = new GradeBook("test");
    Student student = new Student("studentId");
    student.setFirstName(firstName.toUpperCase());
    student.setLastName(lastName.toLowerCase());
    book.addStudent(student);
    book.makeClean();

    assertThat(student.getD2LId(), is(nullValue()));
    assertThat(student.isDirty(), is(false));

    assertThat(grades.findStudentInGradebookForD2LStudent(d2lStudent, book), equalTo(student));
    assertThat(student.getD2LId(), equalTo(d2lId));
    assertThat(student.isDirty(), is(true));
  }

  @Ignore
  @Test
  public void matchStudentDifferentFirstAndLastNameButSameD2LId() {
    String d2lId = "d2lId";
    String firstName = "firstName";
    String lastName = "lastName";

    GradesFromD2L grades = new GradesFromD2L();
    GradesFromD2L.D2LStudent d2lStudent = GradesFromD2L.newStudent().setFirstName(firstName).setLastName(lastName).setD2LId(d2lId).create();
    grades.addStudent(d2lStudent);

    GradeBook book = new GradeBook("test");
    Student student = new Student("studentId");
    student.setFirstName(firstName + "2");
    student.setLastName(lastName + "2");
    student.setD2LId(d2lId);
    book.addStudent(student);
    book.makeClean();

    assertThat(student.getD2LId(), equalTo(d2lId));
    assertThat(student.isDirty(), is(false));

    assertThat(grades.findStudentInGradebookForD2LStudent(d2lStudent, book), equalTo(student));
    assertThat(student.getD2LId(), equalTo(d2lId));
    assertThat(student.isDirty(), is(false));
  }

}
