package edu.pdx.cs410J.grader;

import com.google.common.collect.Lists;
import org.junit.Ignore;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class D2LCSVParserTest {

  @Test
  public void ignoreExpectedColumns() {
    D2LCSVParser parser = new D2LCSVParser(createCsv().getReader());

    assertThat(parser.isColumnIgnored("Calculated Final Grade Numerator"), is(true));
    assertThat(parser.isColumnIgnored("Calculated Final Grade Denominator"), is(true));
    assertThat(parser.isColumnIgnored("Adjusted Final Grade Numerator"), is(true));
    assertThat(parser.isColumnIgnored("Adjusted Final Grade Denominator"), is(true));
    assertThat(parser.isColumnIgnored("End-of-Line Indicator"), is(true));
  }

  @Ignore
  @Test
  public void doNotIgnoreNonIgnoredColumns() {
    D2LCSVParser parser = new D2LCSVParser(createCsv().getReader());

    assertThat(parser.isColumnIgnored("First Name"), is(false));
  }

  @Test
  public void canParseCsvWithNoStudents() {

    D2LCSVParser parser = new D2LCSVParser(createCsv().getReader());
    GradesFromD2L grades = parser.parse();
    assertThat(grades.getStudents(), hasSize(0));
  }

  private CSV createCsv() {
    CSV csv = new CSV();
    csv.addLine("Username", "Last Name", "First Name", "Email", "Programming Background Quiz Points Grade <Numeric MaxPoints:4>", "Java Language and OOP Quiz Points Grade <Numeric MaxPoints:4>", "Language API Quiz Points Grade <Numeric MaxPoints:4>", "Java IO and Collections Quiz Points Grade <Numeric MaxPoints:4>", "Web and REST Quiz Points Grade <Numeric MaxPoints:4>", "Google Web Toolkit Quiz Points Grade <Numeric MaxPoints:4>", "Calculated Final Grade Numerator", "Calculated Final Grade Denominator", "Adjusted Final Grade Numerator", "Adjusted Final Grade Denominator", "End-of-Line Indicator");
    return csv;
  }

  private class CSV {
    List<List<String>> lines = Lists.newArrayList();
    public void addLine(String... cells) {
      lines.add(Arrays.asList(cells));
    }

    public Reader getReader() {
      StringWriter writer = new StringWriter();
      for (List<String> line : lines) {
        for (String cell : line) {
          writer.write(cell);
          writer.write(",");
        }
        writer.write("\n");
      }

      return new StringReader(writer.toString());
    }
  }

  // No students
  // No assignments
  // No grades
  // Some missing grades
}
