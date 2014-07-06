package edu.pdx.cs410J.grader;

import java.io.Reader;

public class D2LCSVParser {

  private static final String[] ignoredColumnNames = new String[] {
    "Calculated Final Grade Numerator",
    "Calculated Final Grade Denominator",
    "Adjusted Final Grade Numerator",
    "Adjusted Final Grade Denominator",
    "End-of-Line Indicator"
  };

  public D2LCSVParser(Reader reader) {


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
}
