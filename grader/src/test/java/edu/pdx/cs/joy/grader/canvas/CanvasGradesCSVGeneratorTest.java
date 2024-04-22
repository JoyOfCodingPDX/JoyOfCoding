package edu.pdx.cs.joy.grader.canvas;

import edu.pdx.cs.joy.grader.gradebook.Assignment;
import edu.pdx.cs.joy.grader.gradebook.GradeBook;
import edu.pdx.cs.joy.grader.gradebook.Student;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class CanvasGradesCSVGeneratorTest {

  @Test
  void canvasCSVHasStudentName() throws IOException {
    GradeBook book = new GradeBook("Test");
    String firstName = "First Name";
    String lastName = "Last Name";
    book.addStudent(new Student("id").setFirstName(firstName).setLastName(lastName));

    CanvasGradesCSVParser parser = convertToCSVAndBack(book);
    List<GradesFromCanvas.CanvasStudent> students = parser.getGrades().getStudents();
    assertThat(students, hasSize(1));

    GradesFromCanvas.CanvasStudent student = students.get(0);
    assertThat(student.getFirstName(), equalTo(firstName));
    assertThat(student.getLastName(), equalTo(lastName));
  }

  @Test
  void canvasCSVHasStudentCanvasId() throws IOException {
    GradeBook book = new GradeBook("Test");
    String canvasId = "Canvas Id";
    book.addStudent(new Student("id")
      .setFirstName("First Name")
      .setLastName("Last Name")
      .setCanvasId(canvasId));

    CanvasGradesCSVParser parser = convertToCSVAndBack(book);
    List<GradesFromCanvas.CanvasStudent> students = parser.getGrades().getStudents();
    assertThat(students, hasSize(1));

    GradesFromCanvas.CanvasStudent student = students.get(0);
    assertThat(student.getCanvasId(), equalTo(canvasId));
  }

  @Test
  void canvasCSVHasStudentSection() throws IOException {
    GradeBook book = new GradeBook("Test");
    String sectionName = "Section Name";
    book.setSectionName(Student.Section.UNDERGRADUATE, sectionName);
    book.addStudent(new Student("id")
      .setFirstName("First Name")
      .setLastName("Last Name")
      .setCanvasId("Canvas Id")
      .setEnrolledSection(Student.Section.UNDERGRADUATE));

    CanvasGradesCSVParser parser = convertToCSVAndBack(book);
    List<GradesFromCanvas.CanvasStudent> students = parser.getGrades().getStudents();
    assertThat(students, hasSize(1));

    GradesFromCanvas.CanvasStudent student = students.get(0);
    assertThat(student.getSection(), equalTo(sectionName));
  }

  @Test
  void canvasCSVHasAssignment() throws IOException {
    GradeBook book = new GradeBook("Test");
    String assignmentName = "Test Assignment";
    double points = 3.0;
    int canvasId = 12345;
    book.addAssignment(new Assignment(assignmentName, points).setCanvasId(canvasId));

    CanvasGradesCSVParser parser = convertToCSVAndBack(book);
    List<GradesFromCanvas.CanvasAssignment> assignments = parser.getAssignments();

    assertThat(assignments, hasSize(1));

    GradesFromCanvas.CanvasAssignment canvasAssignment = assignments.get(0);
    assertThat(canvasAssignment.getName(), equalTo(assignmentName));
    assertThat(canvasAssignment.getPointsPossible(), equalTo(points));
    assertThat(canvasAssignment.getId(), equalTo(canvasId));
  }

  private CanvasGradesCSVParser convertToCSVAndBack(GradeBook book) throws IOException {
    StringWriter sw = new StringWriter();
    CanvasGradesCSVGenerator generator = new CanvasGradesCSVGenerator(sw);
    generator.generate(book);

    String csv = sw.toString();
    return new CanvasGradesCSVParser(new StringReader(csv));
  }
}
