package edu.pdx.cs410J.grader;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class D2LCSVParserTest {

  @Test
  public void ignoreExpectedColumns() {
    D2LCSVParser parser = new D2LCSVParser();

    assertThat(parser.isColumnIgnored("Calculated Final Grade Numerator"), is(true));
    assertThat(parser.isColumnIgnored("Calculated Final Grade Denominator"), is(true));
    assertThat(parser.isColumnIgnored("Adjusted Final Grade Numerator"), is(true));
    assertThat(parser.isColumnIgnored("Adjusted Final Grade Denominator"), is(true));
    assertThat(parser.isColumnIgnored("End-of-Line Indicator"), is(true));
  }

  @Ignore
  @Test
  public void doNotIgnoreNonIgnoredColumns() {
    D2LCSVParser parser = new D2LCSVParser();

    assertThat(parser.isColumnIgnored("First Name"), is(false));
  }

  // No students
  // No assignments
  // No grades
  // Some missing grades
}
