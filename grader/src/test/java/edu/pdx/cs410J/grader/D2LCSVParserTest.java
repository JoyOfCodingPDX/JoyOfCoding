package edu.pdx.cs410J.grader;

import com.google.common.collect.Lists;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

public class D2LCSVParserTest {

  @Test
  public void ignoreExpectedColumns() throws IOException {
    D2LCSVParser parser = new D2LCSVParser(createCsv().getReader());

    assertThat(parser.isColumnIgnored("Calculated Final Grade Numerator"), is(true));
    assertThat(parser.isColumnIgnored("Calculated Final Grade Denominator"), is(true));
    assertThat(parser.isColumnIgnored("Adjusted Final Grade Numerator"), is(true));
    assertThat(parser.isColumnIgnored("Adjusted Final Grade Denominator"), is(true));
    assertThat(parser.isColumnIgnored("End-of-Line Indicator"), is(true));
  }

  @Test
  public void doNotIgnoreNonIgnoredColumns() throws IOException {
    D2LCSVParser parser = new D2LCSVParser(createCsv().getReader());

    assertThat(parser.isColumnIgnored("Username"), is(false));
    assertThat(parser.isColumnIgnored("First Name"), is(false));
    assertThat(parser.isColumnIgnored("Last Name"), is(false));
    assertThat(parser.isColumnIgnored("Email"), is(false));
  }

  @Test
  public void canParseCsvWithNoStudents() throws IOException {
    D2LCSVParser parser = new D2LCSVParser(createCsv().getReader());
    GradesFromD2L grades = parser.parse();
    assertThat(grades.getStudents(), hasSize(0));
  }

  @Test
  public void usernameIsFirstColumn() throws IOException {
    D2LCSVParser parser = new D2LCSVParser(createCsv().getReader());
    assertThat(parser.getUsernameColumn(), equalTo(0));
  }

  @Test
  public void lastNameIsSecondColumn() throws IOException {
    D2LCSVParser parser = new D2LCSVParser(createCsv().getReader());
    assertThat(parser.getLastNameColumn(), equalTo(1));
  }

  @Test
  public void firstNameIsThirdColumn() throws IOException {
    D2LCSVParser parser = new D2LCSVParser(createCsv().getReader());
    assertThat(parser.getFirstNameColumn(), equalTo(2));
  }

  @Test
  public void emailIsFourthColumn() throws IOException {
    D2LCSVParser parser = new D2LCSVParser(createCsv().getReader());
    assertThat(parser.getEmailColumn(), equalTo(3));
  }

  @Test
  public void expectedQuizNames() throws IOException {
    D2LCSVParser parser = new D2LCSVParser(createCsv().getReader());
    assertThat(parser.getQuizNames(), hasItem("Programming Background Quiz"));
    assertThat(parser.getQuizNames(), hasItem("Java Language and OOP Quiz"));
    assertThat(parser.getQuizNames(), hasItem("Language API Quiz"));
  }

  @Test
  public void quizNameDoNotContainIgnoredColumns() throws IOException {
    D2LCSVParser parser = new D2LCSVParser(createCsv().getReader());

    assertThat(parser.getQuizNames(), not(hasItem("Calculated Final Grade Numerator")));
    assertThat(parser.getQuizNames(), not(hasItem("Calculated Final Grade Denominator")));
    assertThat(parser.getQuizNames(), not(hasItem("Adjusted Final Grade Numerator")));
    assertThat(parser.getQuizNames(), not(hasItem("Adjusted Final Grade Denominator")));
    assertThat(parser.getQuizNames(), not(hasItem("Adjusted Final Grade Denominator")));
    assertThat(parser.getQuizNames(), not(hasItem("End-of-Line Indicator")));
  }


  @Test
  public void studentInformationIsNotConsideredAQuiz() throws IOException {
    D2LCSVParser parser = new D2LCSVParser(createCsv().getReader());

    assertThat(parser.getQuizNames(), not(hasItem("Username")));
    assertThat(parser.getQuizNames(), not(hasItem("First Name")));
    assertThat(parser.getQuizNames(), not(hasItem("Last Name")));
    assertThat(parser.getQuizNames(), not(hasItem("Email")));
  }

  @Test
  public void gradesPopulatedWithStudents() throws IOException {
    D2LCSVParser parser = new D2LCSVParser(createCsv().getReader());

    GradesFromD2L grades = parser.getGrades();
    assertThat(grades.getStudents(), hasStudentWithFirstName("Student"));
  }

  private Matcher<? super List<GradesFromD2L.D2LStudent>> hasStudentWithFirstName(String firstName) {
    return new TypeSafeMatcher<List<GradesFromD2L.D2LStudent>>() {
      @Override
      protected boolean matchesSafely(List<GradesFromD2L.D2LStudent> students) {
        for (GradesFromD2L.D2LStudent student : students) {
          if (student.getFirstName().equals(firstName)) {
            return true;
          }
        }

        return false;
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("a student with a first name of ").appendValue(firstName);
      }

      @Override
      protected void describeMismatchSafely(List<GradesFromD2L.D2LStudent> students, Description mismatchDescription) {
        mismatchDescription.appendText("the students' first names were: ");
        Stream<String> firstNames = students.stream().map(GradesFromD2L.D2LStudent::getFirstName);
        mismatchDescription.appendValueList("", ",", "", toIterable(firstNames));
      }

      private <T> Iterable<T> toIterable(final Stream<T> firstNames) {
        return new Iterable<T>() {
          @Override
          public Iterator<T> iterator() {
            return firstNames.iterator();
          }

          @Override
          public void forEach(Consumer<? super T> action) {
            firstNames.forEach(action);
          }

          @Override
          public Spliterator<T> spliterator() {
            return firstNames.spliterator();
          }
        };
      }
    };
  }

  private CSV createCsv() {
    CSV csv = new CSV();
    csv.addLine("Username", "Last Name", "First Name", "Email", "Programming Background Quiz Points Grade <Numeric MaxPoints:4>", "Java Language and OOP Quiz Points Grade <Numeric MaxPoints:4>", "Language API Quiz Points Grade <Numeric MaxPoints:4>", "Java IO and Collections Quiz Points Grade <Numeric MaxPoints:4>", "Web and REST Quiz Points Grade <Numeric MaxPoints:4>", "Google Web Toolkit Quiz Points Grade <Numeric MaxPoints:4>", "Calculated Final Grade Numerator", "Calculated Final Grade Denominator", "Adjusted Final Grade Numerator", "Adjusted Final Grade Denominator", "End-of-Line Indicator");
    csv.addLine("student1","One","Student","student1@email.com","4","","","","","","4","24","","","");
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
