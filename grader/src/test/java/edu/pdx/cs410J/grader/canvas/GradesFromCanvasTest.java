package edu.pdx.cs410J.grader.canvas;

import edu.pdx.cs410J.grader.Assignment;
import edu.pdx.cs410J.grader.GradeBook;
import edu.pdx.cs410J.grader.Student;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GradesFromCanvasTest extends CanvasTestCase {

  @Test
  public void noStudentInGradebookThatMatchesD2LStudent() {
    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2lStudent = createCanvasStudent();
    grades.addStudent(d2lStudent);

    GradeBook book = new GradeBook("test");
    Student student = new Student("studentId");
    student.setCanvasId("notD2LId");
    book.addStudent(student);

    assertThat(grades.findStudentInGradebookForCanvasStudent(d2lStudent, book).isPresent(), is(false));
  }

  @Test
  public void matchStudentByCanvasId() {
    GradesFromCanvas grades = new GradesFromCanvas();
    String canvasId = "d2lId";
    GradesFromCanvas.CanvasStudent d2lStudent = createCanvasStudent("firstName", "lastName", "logId", canvasId);
    grades.addStudent(d2lStudent);

    GradeBook book = new GradeBook("test");
    Student student = new Student("studentId");
    student.setCanvasId(canvasId);
    book.addStudent(student);

    Optional<Student> found = grades.findStudentInGradebookForCanvasStudent(d2lStudent, book);
    assertThat(found.isPresent(), equalTo(true));
    assertThat(found.get(), equalTo(student));
  }

  @Test
  public void matchStudentByIdAndCanvasId() {
    String studentId = "studentId";
    String canvasId = "canvasId";

    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent canvasStudent = createCanvasStudent("First Name", "Last Name", studentId, canvasId);
    grades.addStudent(canvasStudent);

    GradeBook book = new GradeBook("test");
    Student student = new Student(studentId);
    book.addStudent(student);

    assertThat(grades.findStudentInGradebookForCanvasStudent(canvasStudent, book).get(), equalTo(student));
    assertThat(student.isDirty(), is(true));
    assertThat(student.getCanvasId(), equalTo(canvasId));
  }

  @Test
  public void matchStudentByFirstAndLastName() {
    String firstName = "firstName";
    String lastName = "lastName";
    String canvasId = "canvasId";

    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2lStudent = createCanvasStudent(firstName, lastName, "loginId", canvasId);
    grades.addStudent(d2lStudent);

    GradeBook book = new GradeBook("test");
    Student student = new Student("studentId");
    student.setFirstName(firstName);
    student.setLastName(lastName);
    book.addStudent(student);
    book.makeClean();

    assertThat(student.getCanvasId(), is(nullValue()));
    assertThat(student.isDirty(), is(false));

    assertThat(grades.findStudentInGradebookForCanvasStudent(d2lStudent, book).get(), equalTo(student));
    assertThat(student.getCanvasId(), equalTo(canvasId));
    assertThat(student.isDirty(), is(true));
  }

  @Test
  public void matchStudentByFirstAndLastNameIgnoringCase() {
    String firstName = "firstName";
    String lastName = "lastName";
    String canvasId = "canvasId";

    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2lStudent = createCanvasStudent(firstName, lastName, "loginId", canvasId);
    grades.addStudent(d2lStudent);

    GradeBook book = new GradeBook("test");
    Student student = new Student("studentId");
    student.setFirstName(firstName.toUpperCase());
    student.setLastName(lastName.toLowerCase());
    book.addStudent(student);
    book.makeClean();

    assertThat(student.getCanvasId(), is(nullValue()));
    assertThat(student.isDirty(), is(false));

    assertThat(grades.findStudentInGradebookForCanvasStudent(d2lStudent, book).get(), equalTo(student));
    assertThat(student.getCanvasId(), equalTo(canvasId));
    assertThat(student.isDirty(), is(true));
  }

  @Test
  public void matchStudentDifferentFirstAndLastNameButSameD2LId() {
    String canvasId = "canvasId";
    String firstName = "firstName";
    String lastName = "lastName";

    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2lStudent = createCanvasStudent(firstName, lastName, "loginId", canvasId);
    grades.addStudent(d2lStudent);

    GradeBook book = new GradeBook("test");
    Student student = new Student("studentId");
    student.setFirstName(firstName + "2");
    student.setLastName(lastName + "2");
    student.setCanvasId(canvasId);
    book.addStudent(student);
    book.makeClean();

    assertThat(student.getCanvasId(), equalTo(canvasId));
    assertThat(student.isDirty(), is(false));

    assertThat(grades.findStudentInGradebookForCanvasStudent(d2lStudent, book).get(), equalTo(student));
    assertThat(student.getCanvasId(), equalTo(canvasId));
    assertThat(student.isDirty(), is(false));
  }

  @Test
  public void gradeIsSet() {
    GradesFromCanvas.CanvasStudent student = createCanvasStudent();
    String quizName = "quizName";
    double score = 3.4;
    student.setScore(quizName, score);

    assertThat(student.getScore(quizName), equalTo(score));
  }

  @Test
  public void matchQuizWithSameName() {
    String quizName = "quizName";

    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2lStudent = createCanvasStudent("first", "last", "loginId", "canvasId");
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
    GradesFromCanvas.CanvasStudent d2lStudent = createCanvasStudent();
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
    GradesFromCanvas.CanvasStudent d2lStudent = createCanvasStudent();
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
    GradesFromCanvas.CanvasStudent d2lStudent = createCanvasStudent();
    grades.addStudent(d2lStudent);
    String canvasQuizName = "Quiz 1: " + quizDescription + " (12345)";
    d2lStudent.setScore(canvasQuizName, 3.6);

    GradeBook book = new GradeBook("test");
    Assignment assignment = new Assignment("quiz1", 4.0);
    assignment.setDescription(quizDescription);
    book.addAssignment(assignment);

    assertThat(grades.findAssignmentInGradebookForCanvasQuiz(canvasQuizName, book).orElseGet(() -> null), equalTo(assignment));

  }

  @Test
  void undergradSectionIsSetFromCanvasGrades() {
    String firstName = "firstName";
    String lastName = "lastName";
    String section = "Section";

    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2lStudent = createCanvasStudent(firstName, lastName, "loginId", "canvasId", section);
    grades.addStudent(d2lStudent);

    GradeBook book = new GradeBook("test");
    Student student = new Student("studentId");
    student.setFirstName(firstName);
    student.setLastName(lastName);
    student.setEnrolledSection(Student.Section.UNDERGRADUATE);
    book.addStudent(student);

    assertThat(grades.findStudentInGradebookForCanvasStudent(d2lStudent, book).get(), equalTo(student));
    assertThat(student.getCanvasId(), equalTo("canvasId"));
    assertThat(book.getSectionName(Student.Section.UNDERGRADUATE), equalTo(section));
  }

}
