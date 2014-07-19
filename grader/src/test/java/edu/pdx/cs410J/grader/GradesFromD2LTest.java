package edu.pdx.cs410J.grader;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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

    assertThat(grades.findStudentInGradebookForD2LStudent(d2lStudent, book).isPresent(), is(false));
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

    assertThat(grades.findStudentInGradebookForD2LStudent(d2lStudent, book).get(), equalTo(student));
  }

  @Test
  public void matchStudentByIdAndD2LId() {
    String studentId = "studentId";

    GradesFromD2L grades = new GradesFromD2L();
    GradesFromD2L.D2LStudent d2lStudent = GradesFromD2L.newStudent().setFirstName("firstName").setLastName("lastName").setD2LId(studentId).create();
    grades.addStudent(d2lStudent);

    GradeBook book = new GradeBook("test");
    Student student = new Student(studentId);
    book.addStudent(student);

    assertThat(grades.findStudentInGradebookForD2LStudent(d2lStudent, book).get(), equalTo(student));
    assertThat(student.isDirty(), is(true));
    assertThat(student.getD2LId(), equalTo(studentId));
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

    assertThat(grades.findStudentInGradebookForD2LStudent(d2lStudent, book).get(), equalTo(student));
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

    assertThat(grades.findStudentInGradebookForD2LStudent(d2lStudent, book).get(), equalTo(student));
    assertThat(student.getD2LId(), equalTo(d2lId));
    assertThat(student.isDirty(), is(true));
  }

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

    assertThat(grades.findStudentInGradebookForD2LStudent(d2lStudent, book).get(), equalTo(student));
    assertThat(student.getD2LId(), equalTo(d2lId));
    assertThat(student.isDirty(), is(false));
  }

  @Test
  public void gradeIsSet() {
    GradesFromD2L.D2LStudent student = GradesFromD2L.newStudent().setFirstName("first").setLastName("last").setD2LId("id").create();
    String quizName = "quizName";
    double score = 3.4;
    student.setScore(quizName, score);

    assertThat(student.getScore(quizName), equalTo(score));
  }

  @Test
  public void matchQuizWithSameName() {
    String quizName = "quizName";

    GradesFromD2L grades = new GradesFromD2L();
    GradesFromD2L.D2LStudent d2lStudent = GradesFromD2L.newStudent().setFirstName("first").setLastName("last").setD2LId("id").create();
    grades.addStudent(d2lStudent);
    d2lStudent.setScore(quizName, 3.6);

    GradeBook book = new GradeBook("test");
    Assignment assignment = new Assignment(quizName, 4.0);
    book.addAssignment(assignment);

    assertThat(grades.findAssignmentInGradebookForD2lQuiz(quizName, book).get(), equalTo(assignment));
  }

  @Test
  public void matchQuizWithPrefixThatIsTheSameAsQuizNameInD2L() {
    String quizName = "quizName";

    GradesFromD2L grades = new GradesFromD2L();
    GradesFromD2L.D2LStudent d2lStudent = GradesFromD2L.newStudent().setFirstName("first").setLastName("last").setD2LId("id").create();
    grades.addStudent(d2lStudent);
    d2lStudent.setScore(quizName + " Quiz", 3.6);

    GradeBook book = new GradeBook("test");
    Assignment assignment = new Assignment(quizName, 4.0);
    book.addAssignment(assignment);

    assertThat(grades.findAssignmentInGradebookForD2lQuiz(quizName, book).orElseGet(() -> null), equalTo(assignment));

  }

  @Test
  public void matchQuizWithDescriptionPrefixThatIsTheSameAsQuizNameInD2L() {
    String quizName = "quizName";

    GradesFromD2L grades = new GradesFromD2L();
    GradesFromD2L.D2LStudent d2lStudent = GradesFromD2L.newStudent().setFirstName("first").setLastName("last").setD2LId("id").create();
    grades.addStudent(d2lStudent);
    d2lStudent.setScore(quizName + " Quiz", 3.6);

    GradeBook book = new GradeBook("test");
    Assignment assignment = new Assignment("quiz0", 4.0);
    assignment.setDescription(quizName);
    book.addAssignment(assignment);

    assertThat(grades.findAssignmentInGradebookForD2lQuiz(quizName, book).orElseGet(() -> null), equalTo(assignment));

  }

}
