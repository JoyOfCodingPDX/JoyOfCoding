package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class StudentXmlTest {

  @Test
  public void letterGradeIsPersistedInXml() throws TransformerException, ParserException {
    Student student = new Student("test");
    LetterGrade letterGrade = LetterGrade.A;
    student.setLetterGrade(letterGrade);

    Student student2 = writeAndReadStudentAsXml(student);

    assertThat(student2.getLetterGrade(), equalTo(letterGrade));
  }

  private Student writeAndReadStudentAsXml(Student student) throws TransformerException, ParserException {
    Document doc = XmlDumper.toXml(student);
    byte[] bytes = XmlHelper.getBytesForXmlDocument(doc);

    XmlStudentParser parser = new XmlStudentParser(new InputStreamReader(new ByteArrayInputStream(bytes)));
    return parser.parseStudent();
  }

  @Test
  public void oneSubmissionIsPersistedInXml() throws TransformerException, ParserException {
    Student student = new Student("test");
    String assignmentName = "project";
    LocalDateTime submissionTime = LocalDateTime.now().minusHours(3).withNano(0);
    double estimatedHours = 43.5;

    Grade grade = new Grade(assignmentName, Grade.NO_GRADE);
    grade.noteSubmission(submissionTime).setEstimatedHours(estimatedHours);
    student.setGrade(assignmentName, grade);

    Student student2 = writeAndReadStudentAsXml(student);
    Grade grade2 = student2.getGrade(assignmentName);
    assertThat(grade2.getSubmissionInfos(), hasSize(1));

    Grade.SubmissionInfo info = grade2.getSubmissionInfos().get(0);
    assertThat(info.getSubmissionTime(), equalTo(submissionTime));
    assertThat(info.getEstimatedHours(), equalTo(estimatedHours));
  }

  @Test
  public void multipleSubmissionsArePersistedInXml() throws TransformerException, ParserException {
    Student student = new Student("test");
    String assignmentName = "project";
    LocalDateTime submissionTime1 = LocalDateTime.now().minusHours(3).withNano(0);
    double estimatedHours1 = 24.8;
    LocalDateTime submissionTime2 = LocalDateTime.now().minusHours(4).withNano(0);

    Grade grade = new Grade(assignmentName, Grade.NO_GRADE);
    grade.noteSubmission(submissionTime1).setEstimatedHours(estimatedHours1);
    grade.noteSubmission(submissionTime2);
    student.setGrade(assignmentName, grade);

    Student student2 = writeAndReadStudentAsXml(student);
    Grade grade2 = student2.getGrade(assignmentName);
    assertThat(grade2.getSubmissionInfos(), hasSize(2));

    Grade.SubmissionInfo info1 = grade2.getSubmissionInfos().get(0);
    assertThat(info1.getSubmissionTime(), equalTo(submissionTime1));
    assertThat(info1.getEstimatedHours(), equalTo(estimatedHours1));

    Grade.SubmissionInfo info2 = grade2.getSubmissionInfos().get(1);
    assertThat(info2.getSubmissionTime(), equalTo(submissionTime2));
    assertThat(info2.getEstimatedHours(), nullValue());
  }

  @Test
  public void enrolledUndergraduateSectionIsPersistedInXml() throws TransformerException, ParserException {
    Student student = new Student("test");
    Student.Section section = Student.Section.UNDERGRADUATE;
    student.setEnrolledSection(section);

    Student student2 = writeAndReadStudentAsXml(student);

    assertThat(student2.getEnrolledSection(), equalTo(section));
  }

  @Test
  public void enrolledGraduateSectionIsPersistedInXml() throws TransformerException, ParserException {
    Student student = new Student("test");
    Student.Section section = Student.Section.GRADUATE;
    student.setEnrolledSection(section);

    Student student2 = writeAndReadStudentAsXml(student);

    assertThat(student2.getEnrolledSection(), equalTo(section));
  }

  @Test
  public void xmlHasNewlinesButNotIndentation() throws TransformerException {
    Student student = new Student("test");
    Document doc = XmlDumper.toXml(student);
    byte[] bytes = XmlHelper.getBytesForXmlDocument(doc);
    String string = new String(bytes);
    assertThat(string, containsString("\n"));
    assertThat(string.chars().filter(c -> '\n' == c).count(), greaterThan(4L));
    assertThat(string, not(containsString(" <")));
  }

  @Test
  void canParseXmlFileWithSsn() throws ParserException {
    Reader reader = readTextResource("studentWithSsn.xml");
    XmlStudentParser parser = new XmlStudentParser(reader);
    Student student = parser.parseStudent();
    assertThat(student, notNullValue());
    assertThat(student.getId(), equalTo("studentId"));
  }

  private Reader readTextResource(String resourceName) {
    InputStream resource = getClass().getResourceAsStream(resourceName);
    if (resource == null) {
      throw new IllegalArgumentException("Can't find resource " + resourceName);
    }
    return new InputStreamReader(resource);
  }

  @Test
  void canParseOldXmlFileWithSubmissionTimesInsteadOfSubmissionInfo() throws ParserException {
    XmlStudentParser parser = new XmlStudentParser(readTextResource("studentWithSubmissionDatesAndNoSubmissionInfo.xml"));
    Student student = parser.parseStudent();
    assertThat(student, notNullValue());
    Grade grade = student.getGrade("Project2");
    assertThat(grade, notNullValue());
    List<Grade.SubmissionInfo> submissions = grade.getSubmissionInfos();
    assertThat(submissions, hasSize(3));
  }
}
