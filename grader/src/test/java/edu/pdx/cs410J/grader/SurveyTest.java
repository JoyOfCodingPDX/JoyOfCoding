package edu.pdx.cs410J.grader;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SurveyTest {
  @Test
  void setNonEmptyValue() {
    Student student = new Student("test");
    String nonEmptyFirstName = "firstName";

    Survey.setValueIfNotEmpty(nonEmptyFirstName, student::setFirstName);

    assertThat(student.getFirstName(), equalTo(nonEmptyFirstName));
  }

  @Test
  void setNullValue() {
    Student student = new Student("test");
    String nonNullValue = "nonNullValue";
    student.setFirstName(nonNullValue);

    Survey.setValueIfNotEmpty(null, student::setFirstName);

    assertThat(student.getFirstName(), equalTo(nonNullValue));
  }

  @Test
  void setEmptyValue() {
    Student student = new Student("test");
    String nonEmptyValue = "nonEmptyValue";
    student.setFirstName(nonEmptyValue);

    Survey.setValueIfNotEmpty("", student::setFirstName);

    assertThat(student.getFirstName(), equalTo(nonEmptyValue));
  }

  @Test
  void longMessageIsBrokenInto80CharacterLines() {
    String message = "This is a long sentence that contains more than 80 characters. " +
      "Somewhere in here it should be broken into multiple lines of 80 characters each.";

    String lines = Survey.breakUpInto80CharacterLines(message);

    assertThat(lines, not(startsWith(" ")));
    assertThat(lines, containsString("here\nit"));
  }

  @Test
  void validEmailAddressIsValid() {
    String address = "whitlock@cs.pdx.edu";
    assertThat(Survey.isEmailAddress(address), equalTo(true));
  }

  @Test
  void invalidEmailAddressIsInvalid() {
    String address = "whitlock";
    assertThat(Survey.isEmailAddress(address), equalTo(false));
  }

  @Test
  void successfulSurveyWritesStudentXmlFile() {

  }
}
