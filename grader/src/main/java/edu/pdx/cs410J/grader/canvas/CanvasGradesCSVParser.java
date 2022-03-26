package edu.pdx.cs410J.grader.canvas;

import com.google.common.annotations.VisibleForTesting;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CanvasGradesCSVParser implements CanvasGradesCSVColumnNames {
  private static final Pattern assignmentNamePattern = Pattern.compile("(.+) \\((\\d+)\\)");
  private static final String[] ignoredColumnNames = new String[] {
    "ID",
    "SIS User ID",
    SECTION_COLUMN,
    "Getting Ready Current Points",
    "Getting Ready Final Points",
    "Getting Ready Current Score",
    "Getting Ready Unposted Current Score",
    "Getting Ready Final Score",
    "Getting Ready Unposted Final Score",
    "Assignments Current Points",
    "Assignments Final Points",
    "Assignments Current Score",
    "Assignments Unposted Current Score",
    "Assignments Final Score",
    "Assignments Unposted Final Score",
    "Imported Assignments Current Points",
    "Imported Assignments Final Points",
    "Imported Assignments Current Score",
    "Imported Assignments Unposted Current Score",
    "Imported Assignments Final Score",
    "Imported Assignments Unposted Final Score",
    "Current Points",
    "Final Points",
    "Current Score",
    "Unposted Current Score",
    "Final Score",
    "Unposted Final Score"
  };

  private int studentNameColumn;
  private int studentIdColumn;
  private int canvasIdColumn;
  private int sectionIdColumn;
  private final SortedMap<Integer, Assignment> columnToAssignment = new TreeMap<>();
  private final GradesFromCanvas grades;

  public CanvasGradesCSVParser(Reader reader) throws IOException {
    CSVReader csv = new CSVReader(reader);
    try {
      extractColumnNamesFromFirstLineOfCsv(csv.readNext());
      extractPossiblePointsFromSecondLineOfCsv(csv.readNext());

      grades = new GradesFromCanvas();

      String[] studentLine;
      while ((studentLine = csv.readNext()) != null) {
        addStudentAndGradesFromLineOfCsv(studentLine);
      }

    } catch (CsvValidationException ex) {
      throw new IOException("While parsing CSV", ex);
    }

  }

  private void addStudentAndGradesFromLineOfCsv(String[] studentLine) {
    GradesFromCanvas.CanvasStudent student = createStudentFrom(studentLine);

    if (!student.getFirstName().equals("Test")) {
      this.grades.addStudent(student);
    }

    addGradesFromLineOfCsv(student, studentLine);
  }

  private void addGradesFromLineOfCsv(GradesFromCanvas.CanvasStudent student, String[] studentLine) {
    this.columnToAssignment.forEach((column, assignment) -> {
      String score = studentLine[column];
      if (!isEmptyString(score)) {
        student.setScore(assignment.getName(), parseScore(score));
      }
    });

  }

  private boolean isEmptyString(String score) {
    return "".equals(score);
  }

  private double parseScore(String score) {
    return Double.parseDouble(score);
  }

  private GradesFromCanvas.CanvasStudent createStudentFrom(String[] studentLine) {
    String studentName = studentLine[studentNameColumn];
    Pattern studentNamePattern = Pattern.compile("(.*), (.*)");
    Matcher matcher = studentNamePattern.matcher(studentName);
    if (matcher.matches()) {
      GradesFromCanvas.CanvasStudentBuilder builder = GradesFromCanvas.newStudent();
      builder.setFirstName(matcher.group(2));
      builder.setLastName(matcher.group(1));
      builder.setLoginId(studentLine[studentIdColumn]);
      builder.setCanvasId(studentLine[canvasIdColumn]);
      builder.setSection(studentLine[sectionIdColumn]);

      return builder.create();

    } else {
      throw new IllegalStateException("Can't parse student name \"" + studentName + "\"");
    }
  }

  private void extractPossiblePointsFromSecondLineOfCsv(String[] secondLine) {
    this.columnToAssignment.forEach((column, assignment) -> {
      String possiblePointsText = secondLine[column];
      try {
        assignment.setPossiblePoints(Double.parseDouble(possiblePointsText));

      } catch (NumberFormatException ex) {
        throw new IllegalStateException("Can't parse points \"" + possiblePointsText + "\" for " + assignment.getName());
      }
    });

  }

  private void extractColumnNamesFromFirstLineOfCsv(String[] firstLine) {
    for (int i = 0; i < firstLine.length; i++) {
      String cell = firstLine[i];
      switch (cell) {
        case STUDENT_COLUMN:
          this.studentNameColumn = i;
          break;
        case "SIS Login ID":
          this.studentIdColumn = i;
          break;
        case ID_COLUMN:
          this.canvasIdColumn = i;
          break;
        case SECTION_COLUMN:
          this.sectionIdColumn = i;
          break;
        default:
          if (!isColumnIgnored(cell)) {
            addAssignment(cell, i);
          }
      }
    }

  }

  @VisibleForTesting
  static Assignment createAssignment(String assignmentText) {
    Matcher matcher = assignmentNamePattern.matcher(assignmentText);
    if (matcher.matches()) {
      String name = matcher.group(1);
      String idText = matcher.group(2);
      return new Assignment(name, Integer.parseInt(idText));

    } else {
      throw new IllegalStateException("Can't create Assignment from \"" + assignmentText + "\"");
    }
  }

  private void addAssignment(String assignmentText, int column) {
    this.columnToAssignment.put(column, createAssignment(assignmentText));

  }

  private boolean isColumnIgnored(String columnName) {
    for (String ignored : ignoredColumnNames) {
      if (ignored.equals(columnName)) {
        return true;
      }
    }
    return false;
  }

  public List<Assignment> getAssignments() {
    return new ArrayList<>(this.columnToAssignment.values());
  }

  public GradesFromCanvas getGrades() {
    return this.grades;
  }

  public static class Assignment {
    private final String name;
    private final int id;
    private double pointsPossible;

    public Assignment(String name, int id) {
      this.name = name;
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public int getId() {
      return id;
    }

    public double getPointsPossible() {
      return pointsPossible;
    }

    void setPossiblePoints(double possiblePoints) {
      this.pointsPossible = possiblePoints;
    }
  }

}
