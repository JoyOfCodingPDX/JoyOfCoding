package edu.pdx.cs410J.grader;

import org.junit.Test;

import java.io.File;

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

  @Test
  public void canSubmitClassesInStudentPackage() {
    String userId = "student";

    File file = makeFileWithPath("edu", "pdx", "cs410J", userId, "ProjectClass.java");
    Submit submit = new Submit();
    submit.setUserId(userId);
    assertThat(submit.isInEduPdxCs410JDirectory(file), equalTo(true));
  }

  private File makeFileWithPath(String... path) {
    File dir = new File(System.getProperty("user.dir"));

    for (String part : path) {
      dir = new File(dir, part);
    }

    return dir;
  }

  @Test
  public void canSubmitClassesInSubPackage() {
    String userId = "student";

    File file = makeFileWithPath("edu", "pdx", "cs410J", userId, "subpackage", "ProjectClass.java");
    Submit submit = new Submit();
    submit.setUserId(userId);
    assertThat(submit.isInEduPdxCs410JDirectory(file), equalTo(true));
  }
}
