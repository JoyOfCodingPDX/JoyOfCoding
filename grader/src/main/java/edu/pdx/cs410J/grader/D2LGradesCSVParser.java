package edu.pdx.cs410J.grader;

import au.com.bytecode.opencsv.CSVReader;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class D2LGradesCSVParser {

  private static final String[] ignoredColumnNames = new String[] {
    "Calculated Final Grade Numerator",
    "Calculated Final Grade Denominator",
    "Adjusted Final Grade Numerator",
    "Adjusted Final Grade Denominator",
    "End-of-Line Indicator"
  };
  private final GradesFromD2L grades;
  private int usernameColumn = -1;
  private int lastNameColumn = -1;
  private int firstNameColumn = -1;
  private int emailColumn;
  private Map<Integer, String> quizColumnsAndNames = new HashMap<>();

  public D2LGradesCSVParser(Reader reader) throws IOException {
    CSVReader csv = new CSVReader( reader );
    String[] firstLine = csv.readNext();
    extractColumnNamesFromFirstLineOfCsv(firstLine);

    grades = new GradesFromD2L();

    String[] studentLine;
    while ((studentLine = csv.readNext()) != null) {
      addStudentAndGradesFromLineOfCsv(studentLine);
    }
  }

  private void addStudentAndGradesFromLineOfCsv(String[] line) {
    GradesFromD2L.D2LStudentBuilder builder = GradesFromD2L.newStudent();
    builder.setFirstName(getFirstNameFromLine(line));
    builder.setLastName(getLastNameFromLine(line));
    builder.setD2LId(getUsernameFromLine(line));

    GradesFromD2L.D2LStudent student = builder.create();

    if (student.getD2lId().startsWith("guest")) {
      return;
    }

    this.grades.addStudent(student);

    addGradesFromLineOfCsv(student, line);
  }

  private void addGradesFromLineOfCsv(GradesFromD2L.D2LStudent student, String[] line) {
    this.quizColumnsAndNames.forEach((index, quizName) -> {
      String score = line[index];
      if (!isEmptyString(score)) {
        student.setScore(quizName, parseScore(score));
      }
    });

  }

  private boolean isEmptyString(String score) {
    return "".equals(score);
  }

  private double parseScore(String score) {
    return Double.parseDouble(score);
  }

  private String getUsernameFromLine(String[] line) {
    String username = line[getUsernameColumn()];
    if (username.charAt(0) == '#') {
      username = username.substring(1);
    }
    return username;
  }

  private String getLastNameFromLine(String[] line) {
    return line[getLastNameColumn()];
  }

  private String getFirstNameFromLine(String[] line) {
    return line[getFirstNameColumn()];
  }

  private void extractColumnNamesFromFirstLineOfCsv(String[] firstLine) {
    for (int i = 0; i < firstLine.length; i++) {
      String cell = firstLine[i];
      switch (cell) {
        case "Username":
          usernameColumn = i;
          break;
        case "Last Name":
          lastNameColumn = i;
          break;
        case "First Name":
          firstNameColumn = i;
          break;
        case "Email":
          emailColumn = i;
          break;
        default:
          if (!isColumnIgnored(cell)) {
            addQuiz(extractQuizName(cell), i);
          }
      }
    }

  }

  private void addQuiz(String quizName, int column) {
    quizColumnsAndNames.put(column, quizName);
  }

  private String extractQuizName(String cell) {
    // Programming Background Quiz Points Grade <Numeric MaxPoints:4>
    int index = cell.indexOf(" Points Grade");
    if (index > -1) {
      return cell.substring(0, index);

    } else {
      return cell;
    }
  }

  public boolean isColumnIgnored(String columnName) {
    return contains(ignoredColumnNames, columnName);
  }

  private boolean contains(String[] ignoredColumnNames, String columnName) {
    for (String ignored : ignoredColumnNames) {
      if (ignored.equals(columnName)) {
        return true;
      }
    }

    return false;
  }

  public int getUsernameColumn() {
    return usernameColumn;
  }

  public int getLastNameColumn() {
    return lastNameColumn;
  }

  public int getFirstNameColumn() {
    return firstNameColumn;
  }

  public int getEmailColumn() {
    return emailColumn;
  }

  public Iterable<String> getQuizNames() {
    return quizColumnsAndNames.values();
  }

  public GradesFromD2L getGrades() {
    return grades;
  }
}
