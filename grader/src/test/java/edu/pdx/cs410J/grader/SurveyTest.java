package edu.pdx.cs410J.grader;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SurveyTest {
  @Test
  public void setNonEmptyValue() {
    Student student = new Student("test");
    String nonEmptyFirstName = "firstName";

    Survey.setValueIfNotEmpty(nonEmptyFirstName, student::setFirstName);

    assertThat(student.getFirstName(), equalTo(nonEmptyFirstName));
  }

  @Test
  public void setNullValue() {
    Student student = new Student("test");
    String nonNullValue = "nonNullValue";
    student.setFirstName(nonNullValue);

    Survey.setValueIfNotEmpty(null, student::setFirstName);

    assertThat(student.getFirstName(), equalTo(nonNullValue));
  }

  @Test
  public void setEmptyValue() {
    Student student = new Student("test");
    String nonEmptyValue = "nonEmptyValue";
    student.setFirstName(nonEmptyValue);

    Survey.setValueIfNotEmpty("", student::setFirstName);

    assertThat(student.getFirstName(), equalTo(nonEmptyValue));
  }
}
