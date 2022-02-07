package edu.pdx.cs410J.grader.canvas;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CanvasGradesCSVParserTest {

  @Test
  void canCreateAssignmentFromText() {
    String assignmentText = "Name (123)";
    CanvasGradesCSVParser.Assignment assignment = CanvasGradesCSVParser.createAssignment(assignmentText);
    assertThat(assignment.getName(), equalTo("Name"));
    assertThat(assignment.getId(), equalTo(123));
  }

  @Test
  void canCreateAssignmentFromMultiWordText() {
    String assignmentText = "Two Names (123)";
    CanvasGradesCSVParser.Assignment assignment = CanvasGradesCSVParser.createAssignment(assignmentText);
    assertThat(assignment.getName(), equalTo("Two Names"));
    assertThat(assignment.getId(), equalTo(123));
  }

  @Test
  void canReadAssignmentsFromCSV() throws IOException {
    CanvasGradesCSVParser parser = new CanvasGradesCSVParser(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("canvas.csv"))));
    List<CanvasGradesCSVParser.Assignment> assignments = parser.getAssignments();
    assertThat(assignments.get(0).getName(), equalTo("End of Term Survey"));
    assertThat(assignments.get(0).getId(), equalTo(95625));
    assertThat(assignments.get(0).getPointsPossible(), equalTo(3.00));
    assertThat(assignments.get(1).getName(), equalTo("Midterm Survey"));
    assertThat(assignments.get(1).getId(), equalTo(95626));
    assertThat(assignments.get(1).getPointsPossible(), equalTo(3.00));
    assertThat(assignments.get(2).getName(), equalTo("Project 1 POA"));
    assertThat(assignments.get(2).getId(), equalTo(187669));
    assertThat(assignments.get(2).getPointsPossible(), equalTo(1.00));
  }

  @Test
  void canReadStudentsFromCSV() throws IOException {
    CanvasGradesCSVParser parser = new CanvasGradesCSVParser(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("canvas.csv"))));
    List<CanvasGradesCSVParser.Student> students = parser.getStudents();
    assertThat(students.get(0).getFirstName(), equalTo("First1"));
    assertThat(students.get(0).getLastName(), equalTo("Last1"));
    assertThat(students.get(0).getId(), equalTo("student1"));
    assertThat(students.get(1).getFirstName(), equalTo("First2"));
    assertThat(students.get(1).getLastName(), equalTo("Last2"));
    assertThat(students.get(1).getId(), equalTo("student2"));
  }

  @Test
  void testStudentIsNotReadFromCSV() throws IOException {
    CanvasGradesCSVParser parser = new CanvasGradesCSVParser(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("canvas.csv"))));
    List<CanvasGradesCSVParser.Student> students = parser.getStudents();
    assertThat(students, hasSize(2));
  }

  @Test
  void canReadGradesFromCSV() throws IOException {
    CanvasGradesCSVParser parser = new CanvasGradesCSVParser(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("canvas.csv"))));
    List<CanvasGradesCSVParser.Assignment> assignments = parser.getAssignments();
    List<CanvasGradesCSVParser.Student> students = parser.getStudents();

    CanvasGradesCSVParser.Assignment quiz1 = getAssignment(assignments, "Quiz 1: Programming Background");
    assertThat(students.get(0).getScore(quiz1), equalTo(3.00));
    assertThat(students.get(1).getScore(quiz1), equalTo(2.85));

    CanvasGradesCSVParser.Assignment quiz2 = getAssignment(assignments, "Quiz 2: Java Language and OOP");
    assertThat(students.get(0).getScore(quiz2), nullValue());
    assertThat(students.get(1).getScore(quiz2), equalTo(3.00));
  }

  private CanvasGradesCSVParser.Assignment getAssignment(List<CanvasGradesCSVParser.Assignment> assignments, String assignmentName) {
    Optional<CanvasGradesCSVParser.Assignment> optional = assignments.stream().filter(assignment -> assignment.getName().equals(assignmentName)).findFirst();
    return optional.orElseThrow(() -> new IllegalStateException("Can't find assignment \"" + assignmentName + "\""));
  }
}
