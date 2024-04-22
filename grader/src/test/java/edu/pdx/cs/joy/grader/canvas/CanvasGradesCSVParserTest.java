package edu.pdx.cs.joy.grader.canvas;

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
    GradesFromCanvas.CanvasAssignment assignment = CanvasGradesCSVParser.createAssignment(assignmentText);
    assertThat(assignment.getName(), equalTo("Name"));
    assertThat(assignment.getId(), equalTo(123));
  }

  @Test
  void canCreateAssignmentFromMultiWordText() {
    String assignmentText = "Two Names (123)";
    GradesFromCanvas.CanvasAssignment assignment = CanvasGradesCSVParser.createAssignment(assignmentText);
    assertThat(assignment.getName(), equalTo("Two Names"));
    assertThat(assignment.getId(), equalTo(123));
  }

  @Test
  void canReadAssignmentsFromCSV() throws IOException {
    CanvasGradesCSVParser parser = createParserFromResource();
    List<GradesFromCanvas.CanvasAssignment> assignments = parser.getAssignments();

    GradesFromCanvas.CanvasAssignment endOfTermSurvey = getAssignment(assignments, "End of Term Survey");
    assertThat(endOfTermSurvey.getId(), equalTo(812131));
    assertThat(endOfTermSurvey.getPointsPossible(), equalTo(3.00));

    GradesFromCanvas.CanvasAssignment midtermSurvey = getAssignment(assignments, "Midterm Survey");
    assertThat(midtermSurvey.getId(), equalTo(812133));
    assertThat(midtermSurvey.getPointsPossible(), equalTo(3.00));

    GradesFromCanvas.CanvasAssignment project1POA = getAssignment(assignments, "Project 1 POA");
    assertThat(project1POA.getId(), equalTo(812140));
    assertThat(project1POA.getPointsPossible(), equalTo(1.00));
  }

  private CanvasGradesCSVParser createParserFromResource() throws IOException {
    return new CanvasGradesCSVParser(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("canvas.csv"))));
  }

  @Test
  void canReadStudentsFromCSV() throws IOException {
    CanvasGradesCSVParser parser = createParserFromResource();
    List<GradesFromCanvas.CanvasStudent> students = parser.getGrades().getStudents();
    assertThat(students.get(0).getFirstName(), equalTo("First1"));
    assertThat(students.get(0).getLastName(), equalTo("Last1"));
    assertThat(students.get(0).getLoginId(), equalTo("student1"));
    assertThat(students.get(0).getCanvasId(), equalTo("11111"));
    assertThat(students.get(0).getSection(), equalTo("CS-410P-009: TOP: Cnt Sw Dev Java & Android"));
    assertThat(students.get(1).getFirstName(), equalTo("First2"));
    assertThat(students.get(1).getLastName(), equalTo("Last2"));
    assertThat(students.get(1).getLoginId(), equalTo("student2"));
    assertThat(students.get(1).getCanvasId(), equalTo("22222"));
    assertThat(students.get(1).getSection(), equalTo("CS-510-075: TOP: Cnt Sw Dev Java & Android"));
  }

  @Test
  void testStudentIsNotReadFromCSV() throws IOException {
    CanvasGradesCSVParser parser = createParserFromResource();
    List<GradesFromCanvas.CanvasStudent> students = parser.getGrades().getStudents();
    assertThat(students, hasSize(2));
  }

  @Test
  void canReadGradesFromCSV() throws IOException {
    CanvasGradesCSVParser parser = createParserFromResource();
    List<GradesFromCanvas.CanvasAssignment> assignments = parser.getAssignments();
    List<GradesFromCanvas.CanvasStudent> students = parser.getGrades().getStudents();

    GradesFromCanvas.CanvasAssignment quiz1 = getAssignment(assignments, "Quiz 1: Source Code Management with GitHub");
    assertThat(students.get(0).getScore(quiz1), equalTo(3.00));
    assertThat(students.get(1).getScore(quiz1), equalTo(2.85));

    GradesFromCanvas.CanvasAssignment quiz2 = getAssignment(assignments, "Quiz 2: Java Language and OOP");
    assertThat(students.get(0).getScore(quiz2), nullValue());
    assertThat(students.get(1).getScore(quiz2), equalTo(2.80));
  }

  private GradesFromCanvas.CanvasAssignment getAssignment(List<GradesFromCanvas.CanvasAssignment> assignments, String assignmentName) {
    Optional<GradesFromCanvas.CanvasAssignment> optional = assignments.stream().filter(assignment -> assignment.getName().equals(assignmentName)).findFirst();
    return optional.orElseThrow(() -> new IllegalStateException("Can't find assignment \"" + assignmentName + "\""));
  }
}
