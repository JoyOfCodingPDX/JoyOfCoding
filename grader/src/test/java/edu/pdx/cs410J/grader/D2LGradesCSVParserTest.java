package edu.pdx.cs410J.grader;

import com.google.common.collect.Lists;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class D2LGradesCSVParserTest {

  @Test
  public void ignoreExpectedColumns() throws IOException {
    D2LGradesCSVParser parser = new D2LGradesCSVParser(createCsv().getReader());

    assertThat(parser.isColumnIgnored("Calculated Final Grade Numerator"), is(true));
    assertThat(parser.isColumnIgnored("Calculated Final Grade Denominator"), is(true));
    assertThat(parser.isColumnIgnored("Adjusted Final Grade Numerator"), is(true));
    assertThat(parser.isColumnIgnored("Adjusted Final Grade Denominator"), is(true));
    assertThat(parser.isColumnIgnored("End-of-Line Indicator"), is(true));
  }

  @Test
  public void doNotIgnoreNonIgnoredColumns() throws IOException {
    D2LGradesCSVParser parser = new D2LGradesCSVParser(createCsv().getReader());

    assertThat(parser.isColumnIgnored("Username"), is(false));
    assertThat(parser.isColumnIgnored("First Name"), is(false));
    assertThat(parser.isColumnIgnored("Last Name"), is(false));
    assertThat(parser.isColumnIgnored("Email"), is(false));
  }

  @Test
  public void canParseCsvWithNoStudents() throws IOException {
    D2LGradesCSVParser parser = new D2LGradesCSVParser(createEmptyCsv().getReader());
    GradesFromD2L grades = parser.getGrades();
    assertThat(grades.getStudents(), hasSize(0));
  }

  @Test
  public void usernameIsFirstColumn() throws IOException {
    D2LGradesCSVParser parser = new D2LGradesCSVParser(createCsv().getReader());
    assertThat(parser.getUsernameColumn(), equalTo(0));
  }

  @Test
  public void lastNameIsSecondColumn() throws IOException {
    D2LGradesCSVParser parser = new D2LGradesCSVParser(createCsv().getReader());
    assertThat(parser.getLastNameColumn(), equalTo(1));
  }

  @Test
  public void firstNameIsThirdColumn() throws IOException {
    D2LGradesCSVParser parser = new D2LGradesCSVParser(createCsv().getReader());
    assertThat(parser.getFirstNameColumn(), equalTo(2));
  }

  @Test
  public void emailIsFourthColumn() throws IOException {
    D2LGradesCSVParser parser = new D2LGradesCSVParser(createCsv().getReader());
    assertThat(parser.getEmailColumn(), equalTo(3));
  }

  @Test
  public void expectedQuizNames() throws IOException {
    D2LGradesCSVParser parser = new D2LGradesCSVParser(createCsv().getReader());
    assertThat(parser.getQuizNames(), hasItem("Programming Background Quiz"));
    assertThat(parser.getQuizNames(), hasItem("Java Language and OOP Quiz"));
    assertThat(parser.getQuizNames(), hasItem("Language API Quiz"));
  }

  @Test
  public void quizNameDoNotContainIgnoredColumns() throws IOException {
    D2LGradesCSVParser parser = new D2LGradesCSVParser(createCsv().getReader());

    assertThat(parser.getQuizNames(), not(hasItem("Calculated Final Grade Numerator")));
    assertThat(parser.getQuizNames(), not(hasItem("Calculated Final Grade Denominator")));
    assertThat(parser.getQuizNames(), not(hasItem("Adjusted Final Grade Numerator")));
    assertThat(parser.getQuizNames(), not(hasItem("Adjusted Final Grade Denominator")));
    assertThat(parser.getQuizNames(), not(hasItem("Adjusted Final Grade Denominator")));
    assertThat(parser.getQuizNames(), not(hasItem("End-of-Line Indicator")));
  }


  @Test
  public void studentInformationIsNotConsideredAQuiz() throws IOException {
    D2LGradesCSVParser parser = new D2LGradesCSVParser(createCsv().getReader());

    assertThat(parser.getQuizNames(), not(hasItem("Username")));
    assertThat(parser.getQuizNames(), not(hasItem("First Name")));
    assertThat(parser.getQuizNames(), not(hasItem("Last Name")));
    assertThat(parser.getQuizNames(), not(hasItem("Email")));
  }

  @Test
  public void gradesPopulatedWithStudents() throws IOException {
    D2LGradesCSVParser parser = new D2LGradesCSVParser(createCsv().getReader());

    GradesFromD2L grades = parser.getGrades();
    assertThat(grades.getStudents(), hasStudentWithFirstName("Student"));
    assertThat(grades.getStudents(), hasStudentWithLastName("One"));
    assertThat(grades.getStudents(), hasStudentWithD2LId("student1"));
  }

  @Test
  public void gradesPopulatedWithStudents2() throws IOException {
    D2LGradesCSVParser parser = new D2LGradesCSVParser(createCsv().getReader());

    GradesFromD2L grades = parser.getGrades();
    assertThat(grades.getStudents(), hasStudentWithFirstName("Student"));
    assertThat(grades.getStudents(), hasStudentWithLastName("Two"));
    assertThat(grades.getStudents(), hasStudentWithD2LId("student2"));
  }

  @Test
  public void gradesPopulatedWithScores() throws IOException {
    D2LGradesCSVParser parser = new D2LGradesCSVParser(createCsv().getReader());

    GradesFromD2L grades = parser.getGrades();
    Optional<GradesFromD2L.D2LStudent> student = getStudentWithId("student1", grades);
    assertThat("Could not find student", student.isPresent(), is(true));
    assertThat(student.get().getScore("Programming Background Quiz"), equalTo(4.0));
  }

  @Test
  public void gradesPopulatedWithScores2() throws IOException {
    D2LGradesCSVParser parser = new D2LGradesCSVParser(createCsv().getReader());

    GradesFromD2L grades = parser.getGrades();
    Optional<GradesFromD2L.D2LStudent> student = getStudentWithId("student2", grades);
    assertThat("Could not find student", student.isPresent(), is(true));
    assertThat(student.get().getScore("Programming Background Quiz"), equalTo(3.0));
  }

  private java.util.Optional<GradesFromD2L.D2LStudent> getStudentWithId(String d2lId, GradesFromD2L grades) {
    return grades.getStudents().stream().filter(s -> s.getD2lId().equals(d2lId)).findAny();
  }

  private Matcher<? super List<GradesFromD2L.D2LStudent>> hasStudentWithD2LId(String d2lId) {
    return new D2LStudentPropertyMatcher(GradesFromD2L.D2LStudent::getD2lId, "d2l id", d2lId);
  }

  private Matcher<? super List<GradesFromD2L.D2LStudent>> hasStudentWithFirstName(String firstName) {
    return new D2LStudentPropertyMatcher(GradesFromD2L.D2LStudent::getFirstName, "first name", firstName);
  }

  private Matcher<? super List<GradesFromD2L.D2LStudent>> hasStudentWithLastName(String lastName) {
    return new D2LStudentPropertyMatcher(GradesFromD2L.D2LStudent::getLastName, "last name", lastName);
  }

  private CSV createCsv() {
    CSV csv = createEmptyCsv();
    csv.addLine("student1","One","Student","student1@email.com","4","","","","","","4","24","","","");
    csv.addLine("student2","Two","Student","student2@email.com","3","","","","","","4","24","","","");
    return csv;
  }

  private CSV createEmptyCsv() {
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

  @Test
  public void guestStudentIsIgnored() throws IOException {
    CSV csv = createEmptyCsv();
    csv.addLine("guest1234","Two","Student","student2@email.com","3","","","","","","4","24","","","");

    D2LGradesCSVParser parser = new D2LGradesCSVParser(csv.getReader());

    GradesFromD2L grades = parser.getGrades();
    Optional<GradesFromD2L.D2LStudent> student = getStudentWithId("guest1234", grades);
    assertThat(student.isPresent(), equalTo(false));
  }

  @Test
  public void poundSignAtStartOfUsernameIsIgnored() throws IOException {
    CSV csv = createEmptyCsv();
    csv.addLine("#student1","One","Student","student1@email.com","4","","","","","","4","24","","","");
    csv.addLine("student2","Two","Student","student2@email.com","3","","","","","","4","24","","","");

    D2LGradesCSVParser parser = new D2LGradesCSVParser(csv.getReader());

    GradesFromD2L grades = parser.getGrades();
    assertThat(grades.getStudents(), hasStudentWithD2LId("student1"));
    assertThat(grades.getStudents(), hasStudentWithD2LId("student2"));
  }

  // No students
  // No assignments
  // No grades
  // Some missing grades
}
