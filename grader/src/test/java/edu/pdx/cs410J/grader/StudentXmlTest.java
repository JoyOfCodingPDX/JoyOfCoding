package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    Grade grade = new Grade(assignmentName, Grade.NO_GRADE);
    grade.addSubmissionTime(submissionTime);
    student.setGrade(assignmentName, grade);

    Student student2 = writeAndReadStudentAsXml(student);

    assertThat(student2.getGrade(assignmentName).getSubmissionTimes(), contains(submissionTime));
  }

  @Test
  public void multipleSubmissionsArePersistedInXml() throws TransformerException, ParserException {
    Student student = new Student("test");
    String assignmentName = "project";
    LocalDateTime submissionTime1 = LocalDateTime.now().minusHours(3).withNano(0);
    LocalDateTime submissionTime2 = LocalDateTime.now().minusHours(4).withNano(0);

    Grade grade = new Grade(assignmentName, Grade.NO_GRADE);
    grade.addSubmissionTime(submissionTime1);
    grade.addSubmissionTime(submissionTime2);
    student.setGrade(assignmentName, grade);

    Student student2 = writeAndReadStudentAsXml(student);

    assertThat(student2.getGrade(assignmentName).getSubmissionTimes(), contains(submissionTime1, submissionTime2));
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
    InputStream resource = getClass().getResourceAsStream("studentWithSsn.xml");
    XmlStudentParser parser = new XmlStudentParser(new InputStreamReader(resource));
    Student student = parser.parseStudent();
    assertThat(student, notNullValue());
    assertThat(student.getId(), equalTo("studentId"));
  }

  @Test
  void canParseOldXmlFileWithSubmissionTimesInsteadOfSubmissionInfo() throws ParserException {
    InputStream resource = getClass().getResourceAsStream("studentWithSubmissionDatesAndNoSubmissionInfo.xml");
    XmlStudentParser parser = new XmlStudentParser(new InputStreamReader(resource));
    Student student = parser.parseStudent();
    assertThat(student, notNullValue());
    Grade grade = student.getGrade("Project2");
    assertThat(grade, notNullValue());
    List<Grade.SubmissionInfo> submissions = grade.getSubmissionInfos();
    assertThat(submissions, hasSize(3));
  }
}
