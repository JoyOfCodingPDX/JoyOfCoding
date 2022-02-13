package edu.pdx.cs410J.grader.canvas;

import edu.pdx.cs410J.grader.Assignment;
import edu.pdx.cs410J.grader.GradeBook;
import edu.pdx.cs410J.grader.Student;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GradesFromCanvasTest {

  @Test
  public void noStudentInGradebookThatMatchesD2LStudent() {
    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2lStudent = GradesFromCanvas.newStudent().setFirstName("firstName").setLastName("lastName").setLoginId("d2lId").create();
    grades.addStudent(d2lStudent);

    GradeBook book = new GradeBook("test");
    Student student = new Student("studentId");
    student.setD2LId("notD2LId");
    book.addStudent(student);

    assertThat(grades.findStudentInGradebookForCanvasStudent(d2lStudent, book).isPresent(), is(false));
  }

  @Test
  public void matchStudentByD2LId() {
    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2lStudent = GradesFromCanvas.newStudent().setFirstName("firstName").setLastName("lastName").setLoginId("d2lId").create();
    grades.addStudent(d2lStudent);

    GradeBook book = new GradeBook("test");
    Student student = new Student("studentId");
    student.setD2LId("d2lId");
    book.addStudent(student);

    assertThat(grades.findStudentInGradebookForCanvasStudent(d2lStudent, book).get(), equalTo(student));
  }

  @Test
  public void matchStudentByIdAndD2LId() {
    String studentId = "studentId";

    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2lStudent = GradesFromCanvas.newStudent().setFirstName("firstName").setLastName("lastName").setLoginId(studentId).create();
    grades.addStudent(d2lStudent);

    GradeBook book = new GradeBook("test");
    Student student = new Student(studentId);
    book.addStudent(student);

    assertThat(grades.findStudentInGradebookForCanvasStudent(d2lStudent, book).get(), equalTo(student));
    assertThat(student.isDirty(), is(true));
    assertThat(student.getD2LId(), equalTo(studentId));
  }

  @Test
  public void matchStudentByFirstAndLastName() {
    String d2lId = "d2lId";
    String firstName = "firstName";
    String lastName = "lastName";

    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2lStudent = GradesFromCanvas.newStudent().setFirstName(firstName).setLastName(lastName).setLoginId(d2lId).create();
    grades.addStudent(d2lStudent);

    GradeBook book = new GradeBook("test");
    Student student = new Student("studentId");
    student.setFirstName(firstName);
    student.setLastName(lastName);
    book.addStudent(student);
    book.makeClean();

    assertThat(student.getD2LId(), is(nullValue()));
    assertThat(student.isDirty(), is(false));

    assertThat(grades.findStudentInGradebookForCanvasStudent(d2lStudent, book).get(), equalTo(student));
    assertThat(student.getD2LId(), equalTo(d2lId));
    assertThat(student.isDirty(), is(true));
  }

  @Test
  public void matchStudentByFirstAndLastNameIgnoringCase() {
    String d2lId = "d2lId";
    String firstName = "firstName";
    String lastName = "lastName";

    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2lStudent = GradesFromCanvas.newStudent().setFirstName(firstName).setLastName(lastName).setLoginId(d2lId).create();
    grades.addStudent(d2lStudent);

    GradeBook book = new GradeBook("test");
    Student student = new Student("studentId");
    student.setFirstName(firstName.toUpperCase());
    student.setLastName(lastName.toLowerCase());
    book.addStudent(student);
    book.makeClean();

    assertThat(student.getD2LId(), is(nullValue()));
    assertThat(student.isDirty(), is(false));

    assertThat(grades.findStudentInGradebookForCanvasStudent(d2lStudent, book).get(), equalTo(student));
    assertThat(student.getD2LId(), equalTo(d2lId));
    assertThat(student.isDirty(), is(true));
  }

  @Test
  public void matchStudentDifferentFirstAndLastNameButSameD2LId() {
    String d2lId = "d2lId";
    String firstName = "firstName";
    String lastName = "lastName";

    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2lStudent = GradesFromCanvas.newStudent().setFirstName(firstName).setLastName(lastName).setLoginId(d2lId).create();
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

    assertThat(grades.findStudentInGradebookForCanvasStudent(d2lStudent, book).get(), equalTo(student));
    assertThat(student.getD2LId(), equalTo(d2lId));
    assertThat(student.isDirty(), is(false));
  }

  @Test
  public void gradeIsSet() {
    GradesFromCanvas.CanvasStudent student = GradesFromCanvas.newStudent().setFirstName("first").setLastName("last").setLoginId("id").create();
    String quizName = "quizName";
    double score = 3.4;
    student.setScore(quizName, score);

    assertThat(student.getScore(quizName), equalTo(score));
  }

  @Test
  public void matchQuizWithSameName() {
    String quizName = "quizName";

    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2lStudent = GradesFromCanvas.newStudent().setFirstName("first").setLastName("last").setLoginId("id").create();
    grades.addStudent(d2lStudent);
    d2lStudent.setScore(quizName, 3.6);

    GradeBook book = new GradeBook("test");
    Assignment assignment = new Assignment(quizName, 4.0);
    book.addAssignment(assignment);

    assertThat(grades.findAssignmentInGradebookForCanvasQuiz(quizName, book).get(), equalTo(assignment));
  }

  @Test
  public void matchQuizWithPrefixThatIsTheSameAsQuizNameInD2L() {
    String quizName = "quizName";

    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2lStudent = GradesFromCanvas.newStudent().setFirstName("first").setLastName("last").setLoginId("id").create();
    grades.addStudent(d2lStudent);
    d2lStudent.setScore(quizName + " Quiz", 3.6);

    GradeBook book = new GradeBook("test");
    Assignment assignment = new Assignment(quizName, 4.0);
    book.addAssignment(assignment);

    assertThat(grades.findAssignmentInGradebookForCanvasQuiz(quizName, book).orElseGet(() -> null), equalTo(assignment));

  }

  @Test
  public void matchQuizWithDescriptionPrefixThatIsTheSameAsQuizNameInD2L() {
    String quizName = "quizName";

    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2lStudent = GradesFromCanvas.newStudent().setFirstName("first").setLastName("last").setLoginId("id").create();
    grades.addStudent(d2lStudent);
    d2lStudent.setScore(quizName + " Quiz", 3.6);

    GradeBook book = new GradeBook("test");
    Assignment assignment = new Assignment("quiz0", 4.0);
    assignment.setDescription(quizName);
    book.addAssignment(assignment);

    assertThat(grades.findAssignmentInGradebookForCanvasQuiz(quizName, book).orElseGet(() -> null), equalTo(assignment));

  }

  @Test
  void matchQuizWithDescriptionThatIsContainedInQuizNameInCanvas() {
    String quizDescription = "Quiz Description";

    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2lStudent = GradesFromCanvas.newStudent().setFirstName("first").setLastName("last").setLoginId("id").create();
    grades.addStudent(d2lStudent);
    String canvasQuizName = "Quiz 1: " + quizDescription + " (12345)";
    d2lStudent.setScore(canvasQuizName, 3.6);

    GradeBook book = new GradeBook("test");
    Assignment assignment = new Assignment("quiz1", 4.0);
    assignment.setDescription(quizDescription);
    book.addAssignment(assignment);

    assertThat(grades.findAssignmentInGradebookForCanvasQuiz(canvasQuizName, book).orElseGet(() -> null), equalTo(assignment));

  }

}
