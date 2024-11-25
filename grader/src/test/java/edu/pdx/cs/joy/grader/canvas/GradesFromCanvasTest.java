package edu.pdx.cs.joy.grader.canvas;

import edu.pdx.cs.joy.grader.gradebook.Assignment;
import edu.pdx.cs.joy.grader.gradebook.GradeBook;
import edu.pdx.cs.joy.grader.gradebook.Student;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    assertThat(grades.findStudentInGradebookForCanvasStudent(canvasStudent, book).orElse(null), equalTo(student));
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

    assertThat(grades.findStudentInGradebookForCanvasStudent(d2lStudent, book).orElse(null), equalTo(student));
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

    assertThat(grades.findStudentInGradebookForCanvasStudent(d2lStudent, book).orElse(null), equalTo(student));
    assertThat(student.getCanvasId(), equalTo(canvasId));
    assertThat(student.isDirty(), is(true));
  }

  @Test
  public void matchStudentByNickAndLastNameIgnoringCase() {
    String firstName = "firstName";
    String nickName = "nickName";
    String lastName = "lastName";
    String canvasId = "canvasId";

    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2lStudent = createCanvasStudent(nickName, lastName, "loginId", canvasId);
    grades.addStudent(d2lStudent);

    GradeBook book = new GradeBook("test");
    Student student = new Student("studentId");
    student.setFirstName(firstName.toUpperCase());
    student.setLastName(lastName.toLowerCase());
    student.setNickName(nickName.toLowerCase());
    book.addStudent(student);
    book.makeClean();

    assertThat(student.getCanvasId(), is(nullValue()));
    assertThat(student.isDirty(), is(false));

    assertThat(grades.findStudentInGradebookForCanvasStudent(d2lStudent, book).orElse(null), equalTo(student));
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

    assertThat(grades.findStudentInGradebookForCanvasStudent(d2lStudent, book).orElse(null), equalTo(student));
    assertThat(student.getCanvasId(), equalTo(canvasId));
    assertThat(student.isDirty(), is(false));
  }

  @Test
  public void gradeIsSet() {
    GradesFromCanvas.CanvasStudent student = createCanvasStudent();
    String quizName = "quizName";
    GradesFromCanvas.CanvasAssignment quiz = new GradesFromCanvas.CanvasAssignment(quizName, 1234);
    double score = 3.4;
    student.setScore(quiz, score);

    assertThat(student.getScore(quiz), equalTo(score));
  }

  @Test
  public void matchQuizWithSameName() {
    String quizName = "quizName";

    GradesFromCanvas grades = new GradesFromCanvas();
    int canvasId = 1234;
    GradesFromCanvas.CanvasAssignment quiz = new GradesFromCanvas.CanvasAssignment(quizName, canvasId);
    GradesFromCanvas.CanvasStudent d2lStudent = createCanvasStudent("first", "last", "loginId", "canvasId");
    grades.addStudent(d2lStudent);
    d2lStudent.setScore(quiz, 3.6);

    GradeBook book = new GradeBook("test");
    Assignment assignment = new Assignment(quizName, 4.0);
    book.addAssignment(assignment);

    Assignment found = grades.findAssignmentInGradebookForCanvasQuiz(quiz, book).orElseThrow();
    assertThat(found, equalTo(assignment));
    assertThat(found.getCanvasId(), equalTo(canvasId));
  }

  @Test
  public void matchQuizWithPrefixThatIsTheSameAsQuizNameInD2L() {
    String quizName = "quizName";

    GradesFromCanvas grades = new GradesFromCanvas();
    int canvasId = 1234;
    GradesFromCanvas.CanvasAssignment quiz = new GradesFromCanvas.CanvasAssignment(quizName + " Quiz", canvasId);
    GradesFromCanvas.CanvasStudent d2lStudent = createCanvasStudent();
    grades.addStudent(d2lStudent);
    d2lStudent.setScore(quiz, 3.6);

    GradeBook book = new GradeBook("test");
    Assignment assignment = new Assignment(quizName, 4.0);
    book.addAssignment(assignment);

    Assignment found = grades.findAssignmentInGradebookForCanvasQuiz(quiz, book).orElseThrow();
    assertThat(found, equalTo(assignment));
    assertThat(found.getCanvasId(), equalTo(canvasId));
  }

  @Test
  public void matchQuizWithDescriptionPrefixThatIsTheSameAsQuizNameInD2L() {
    String quizName = "quizName";

    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2lStudent = createCanvasStudent();
    GradesFromCanvas.CanvasAssignment quiz = new GradesFromCanvas.CanvasAssignment(quizName + " Quiz", 1234);
    grades.addStudent(d2lStudent);
    d2lStudent.setScore(quiz, 3.6);

    GradeBook book = new GradeBook("test");
    Assignment assignment = new Assignment("quiz0", 4.0);
    assignment.setDescription(quizName);
    book.addAssignment(assignment);

    assertThat(grades.findAssignmentInGradebookForCanvasQuiz(quiz, book).orElse(null), equalTo(assignment));

  }

  @Test
  void matchQuizWithDescriptionThatIsContainedInQuizNameInCanvas() {
    String quizDescription = "Quiz Description";

    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent d2lStudent = createCanvasStudent();
    grades.addStudent(d2lStudent);
    String canvasQuizName = "Quiz 1: " + quizDescription + " (12345)";
    GradesFromCanvas.CanvasAssignment quiz = new GradesFromCanvas.CanvasAssignment(canvasQuizName, 12345);
    d2lStudent.setScore(quiz, 3.6);

    GradeBook book = new GradeBook("test");
    Assignment assignment = new Assignment("quiz1", 4.0);
    assignment.setDescription(quizDescription);
    book.addAssignment(assignment);

    assertThat(grades.findAssignmentInGradebookForCanvasQuiz(quiz, book).orElse(null), equalTo(assignment));

  }

  @Test
  void undergradSectionIsSetFromCanvasGrades() {
    String firstName = "firstName";
    String lastName = "lastName";
    String section = "Section";

    GradesFromCanvas grades = new GradesFromCanvas();
    GradesFromCanvas.CanvasStudent canvasStudent = createCanvasStudent(firstName, lastName, "loginId", "canvasId", section);
    grades.addStudent(canvasStudent);

    GradeBook book = new GradeBook("test");
    Student student = new Student("studentId");
    student.setFirstName(firstName);
    student.setLastName(lastName);
    student.setEnrolledSection(Student.Section.UNDERGRADUATE);
    book.addStudent(student);

    assertThat(grades.findStudentInGradebookForCanvasStudent(canvasStudent, book).orElse(null), equalTo(student));
    assertThat(student.getCanvasId(), equalTo("canvasId"));
    assertThat(book.getSectionName(Student.Section.UNDERGRADUATE), equalTo(section));
  }

//  @Test
//  void assignmentCanvasIdIsSetFromCanvasGrades() {
//    String quizName = "quizName";
//    int quizId = 4567;
//
//    GradesFromCanvas grades = new GradesFromCanvas();
//    CanvasStudent canvasStudent = createCanvasStudent("first", "last", "loginId", "canvasId");
//    grades.addStudent(canvasStudent);
//    canvasStudent.setScore(quizName, 3.6);
//
//    CanvasAssignment canvasAssignment = new CanvasAssignment(quizName, quizId);
//    grades.addAssignment(canvasAssignment);
//
//    GradeBook book = new GradeBook("test");
//    Assignment assignment = new Assignment(quizName, 4.0);
//    book.addAssignment(assignment);
//
//    assertThat(grades.findAssignmentInGradebookForCanvasQuiz(quizName, book).orElse(null), equalTo(assignment));
//    assertThat(book.getAssignment(quizName).getCanvasId(), equalTo(quizId));
//  }

  @Test
  void ambiguousCanvasAssignmentNameThrowsIllegalStateException() {
    GradeBook gradebook = new GradeBook("test");
    String quizName = "restquiz";
    Assignment quiz = new Assignment(quizName, 3.0).setType(Assignment.AssignmentType.QUIZ).setDescription("Web and REST");
    gradebook.addAssignment(quiz);

    String projectName = "Project5";
    Assignment project = new Assignment(projectName, 10.0).setType(Assignment.AssignmentType.PROJECT).setDescription("REST");
    gradebook.addAssignment(project);

    String assignmentInCanvas = "Quiz 5: Web and REST";
    GradesFromCanvas.CanvasAssignment canvasAssignment = new GradesFromCanvas.CanvasAssignment(assignmentInCanvas, 12345);

    GradesFromCanvas canvas = new GradesFromCanvas();
    IllegalStateException ex =
      assertThrows(IllegalStateException.class, () -> canvas.findAssignmentInGradebookForCanvasQuiz(canvasAssignment, gradebook));
    String message = ex.getMessage();
    assertThat(message, containsString(quizName));
    assertThat(message, containsString(projectName));
  }


}
