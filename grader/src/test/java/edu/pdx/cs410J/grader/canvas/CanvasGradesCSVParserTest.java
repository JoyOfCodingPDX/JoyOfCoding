package edu.pdx.cs410J.grader.canvas;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
}
