package edu.pdx.cs410J.grader;

import au.com.bytecode.opencsv.CSVReader;

import java.io.IOException;
import java.io.Reader;

public class D2LCSVParser {

  private static final String[] ignoredColumnNames = new String[] {
    "Calculated Final Grade Numerator",
    "Calculated Final Grade Denominator",
    "Adjusted Final Grade Numerator",
    "Adjusted Final Grade Denominator",
    "End-of-Line Indicator"
  };
  private int usernameColumn = -1;
  private int lastNameColumn = -1;
  private int firstNameColumn = -1;
  private int emailColumn;

  public D2LCSVParser(Reader reader) throws IOException {
    CSVReader csv = new CSVReader( reader );
    String[] firstLine = csv.readNext();
    extractColumnNamesFromFirstLineOfCsv(firstLine);

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
      }
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
}
