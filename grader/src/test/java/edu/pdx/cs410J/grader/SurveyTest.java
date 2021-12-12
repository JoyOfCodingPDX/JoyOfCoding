package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;

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
  void successfulSurveyWritesStudentXmlFile(@TempDir File tempDir) throws IOException, ParserException {
    String firstName = "First name";
    String lastName = "Last name";
    String nickName = "Nick name";
    String loginId = "LoginId";
    String email = "email@email.com";
    String major = "Major";
    String section = "u";
    String learn = "A lot";
    String anythingElse = "Nope";
    String verify = "y";

    InputStream in = getInputStreamWithLinesOfText(firstName, lastName, nickName, loginId, email, major, section, learn, anythingElse, verify);

    Survey survey = new Survey(new TextCapturingOutputStream().getPrintStream(), new TextCapturingOutputStream().getPrintStream(), in, tempDir);
    survey.takeSurvey("-noEmail");

    File xmlFile = new File(tempDir, Survey.STUDENT_XML_FILE_NAME);
    XmlStudentParser parser = new XmlStudentParser(xmlFile);
    Student student = parser.parseStudent();

    assertThat(student.getFirstName(), equalTo(firstName));
    assertThat(student.getLastName(), equalTo(lastName));
    assertThat(student.getNickName(), equalTo(nickName));
    assertThat(student.getId(), equalTo(loginId));
    assertThat(student.getEmail(), equalTo(email));
    assertThat(student.getMajor(), equalTo(major));
    assertThat(student.getEnrolledSection(), equalTo(Student.Section.UNDERGRADUATE));
  }

  private InputStream getInputStreamWithLinesOfText(String... lines) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintWriter pw = new PrintWriter(baos, true);
    for (String line : lines) {
      pw.println(line);
    }
    byte[] bytes = baos.toByteArray();
    return new ByteArrayInputStream(bytes);
  }

  private static class TextCapturingOutputStream {
    private final OutputStream captured = new ByteArrayOutputStream();

    public PrintStream getPrintStream() {
      return new PrintStream(captured);
    }
  }
}
