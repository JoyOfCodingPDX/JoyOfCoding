package edu.pdx.cs410J.grader;

import au.com.bytecode.opencsv.CSVReader;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;

public class D2LCSVParser {

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
  private Collection<String> quizNames = new ArrayList<>();

  public D2LCSVParser(Reader reader) throws IOException {
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
    GradesFromD2L.D2LStudentBuilder builder = new GradesFromD2L.D2LStudentBuilder();
    builder.setFirstName(getFirstNameFromLine(line));
    builder.setLastName(getLastNameFromLine(line));
    builder.setD2LId(getUsernameFromLine(line));

    this.grades.addStudent(builder.create());
  }

  private String getUsernameFromLine(String[] line) {
    return line[getUsernameColumn()];
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
            quizNames.add(extractQuizName(cell));
          }
      }
    }

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


  public GradesFromD2L parse() {
    return new GradesFromD2L();
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
    return quizNames;
  }

  public GradesFromD2L getGrades() {
    return grades;
  }
}
