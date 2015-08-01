package edu.pdx.cs410J.grader;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class SubmitTest {

  @Test
  public void nineDigitStringIsInvalidLoginId() {
    Submit submit = new Submit();
    assertThat(submit.isNineDigitStudentId("123456789"), equalTo(true));
  }

  @Test
  public void eightDigitStringIsValidLoginId() {
    Submit submit = new Submit();
    assertThat(submit.isNineDigitStudentId("12345678"), equalTo(false));
  }

  @Test
  public void userNameIsValidLoginId() {
    Submit submit = new Submit();
    assertThat(submit.isNineDigitStudentId("whitlock"), equalTo(false));
  }

  @Test
  public void emailAddressIsInvalidLoginId() {
    Submit submit = new Submit();
    assertThat(submit.looksLikeAnEmailAddress("me@email.com"), equalTo(true));
  }
}
