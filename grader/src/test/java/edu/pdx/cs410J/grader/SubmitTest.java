package edu.pdx.cs410J.grader;

import org.junit.Test;

import java.io.File;
import java.time.LocalDateTime;

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

  @Test
  public void submissionTimeIsInIsoDateTime() {
    LocalDateTime time = LocalDateTime.of(2018, 7, 8, 5, 59, 32);
    String formattedTime = Submit.ManifestAttributes.formatSubmissionTime(time);
    assertThat(formattedTime, equalTo("2018-07-08T05:59:32"));
  }

  @Test
  public void legacySubmissionTimeCanBeParsed() {
    String legacySubmissionTime = "2018-Jul-08 05:59:32";
    LocalDateTime expected = LocalDateTime.of(2018, 7, 8, 5, 59, 32);

    LocalDateTime actual = Submit.ManifestAttributes.parseSubmissionTime(legacySubmissionTime);
    assertThat(actual, equalTo(expected));
  }
}