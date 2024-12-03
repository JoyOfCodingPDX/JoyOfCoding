package edu.pdx.cs.joy.grader;

import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.grader.gradebook.Student;
import edu.pdx.cs.joy.grader.gradebook.XmlStudentParser;
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
    String address = "whitlocd@pdx.edu";
    assertThat(Survey.isEmailAddress(address), equalTo(true));
  }

  @Test
  void invalidEmailAddressIsInvalid() {
    String address = "whitlock";
    assertThat(Survey.isEmailAddress(address), equalTo(false));
  }

  @Test
  void invalidJavaIdentifierIsInvalid() {
    String id = "123";
    assertThat(Survey.isJavaIdentifier(id), equalTo(false));
  }

  @Test
  void validJavaIdentifierIsValid() {
    String id = "whitlock123";
    assertThat(Survey.isJavaIdentifier(id), equalTo(true));
  }

  @Test
  void successfulSurveyWritesStudentXmlFile(@TempDir File tempDir) throws IOException, ParserException {
    String firstName = "First name";
    String lastName = "Last name";
    String nickName = "Nick name";
    String loginId = "LoginId";
    String email = "email@pdx.edu";
    String major = "Major";
    String section = "u";
    String recordGitHubUserName = "y";
    String learn = "A lot";
    String anythingElse = "Nope";
    String verify = "y";

    String gitHubUserName = "GitHubUserName";

    InputStream in = getInputStreamWithLinesOfText(firstName, lastName, nickName, loginId, email, major, section, recordGitHubUserName, learn, anythingElse, verify);
    createGitConfigWithUserName(tempDir, gitHubUserName);

    TextCapturingOutputStream stdOut = new TextCapturingOutputStream();
    Survey survey = new Survey(stdOut.getPrintStream(), new TextCapturingOutputStream().getPrintStream(), in, tempDir, tempDir);
    survey.takeSurvey("-noEmail");

    String writtenToStdOut = stdOut.getTextThatWasOutput();
    assertThat(writtenToStdOut, containsString("GitHub User Name: " + gitHubUserName));

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
    assertThat(student.getGitHubUserName(), equalTo(gitHubUserName));
  }

  private void createGitConfigWithUserName(File tempDir, String gitHubUserName) throws IOException {
    File dotGit = new File(tempDir, ".git");
    assertThat(dotGit.mkdir(), is(true));
    File config = new File(dotGit, "config");

    try (PrintWriter pw = new PrintWriter(new FileWriter(config))) {
      pw.println("[core]\n" +
        "\trepositoryformatversion = 0\n" +
        "\tfilemode = true\n" +
        "\tbare = false\n" +
        "\tlogallrefupdates = true\n" +
        "\tignorecase = true\n" +
        "\tprecomposeunicode = true\n" +
        "[remote \"origin\"]\n" +
        "\turl = git@github.com:" + gitHubUserName + "/JoyOfCoding.git\n" +
        "\tfetch = +refs/heads/*:refs/remotes/origin/*\n" +
        "[branch \"main\"]\n" +
        "\tremote = origin\n" +
        "\tmerge = refs/heads/main\n");
      pw.flush();
    }
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
    private final ByteArrayOutputStream captured = new ByteArrayOutputStream();

    public PrintStream getPrintStream() {
      return new PrintStream(captured);
    }

    String getTextThatWasOutput() {
      return captured.toString();
    }
  }

  @Test
  void emailAddressIsPdxDotEdu() {
    Student student = new Student("id");
    student.setEmail("student@pdx.edu");
    assertThat(Survey.hasPdxDotEduEmail(student), equalTo(true));
  }

  @Test
  void emailAddressIsNotPdxDotEdu() {
    Student student = new Student("id");
    student.setEmail("student@gmail.com");
    assertThat(Survey.hasPdxDotEduEmail(student), equalTo(false));
  }

}
