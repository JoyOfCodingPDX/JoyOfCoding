package edu.pdx.cs410J.grader.gradebook;

import edu.pdx.cs410J.ParserException;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

import static edu.pdx.cs410J.grader.gradebook.Assignment.ProjectType.*;
import static edu.pdx.cs410J.grader.gradebook.GradeBook.LetterGradeRanges;
import static edu.pdx.cs410J.grader.gradebook.GradeBook.LetterGradeRanges.LetterGradeRange;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GradeBookXmlTest {

  @Test
  public void letterGradeRangesArePersistedToXml() throws IOException, TransformerException, ParserException {
    GradeBook book = new GradeBook("test");

    LetterGradeRanges undergradRanges = book.getLetterGradeRanges(Student.Section.UNDERGRADUATE);
    morphLetterGradeRanges(undergradRanges, 1);

    LetterGradeRanges gradRanges = book.getLetterGradeRanges(Student.Section.GRADUATE);
    morphLetterGradeRanges(gradRanges, 2);

    GradeBook book2 = writeAndReadGradeBookAsXml(book);

    LetterGradeRanges undergradRanges2 = book2.getLetterGradeRanges(Student.Section.UNDERGRADUATE);
    assertRangesAreEqual(undergradRanges, undergradRanges2);

    LetterGradeRanges gradRanges2 = book2.getLetterGradeRanges(Student.Section.GRADUATE);
    assertRangesAreEqual(gradRanges, gradRanges2);
  }

  private void assertRangesAreEqual(LetterGradeRanges ranges, LetterGradeRanges ranges2) {
    for (LetterGrade letterGrade : LetterGrade.values()) {
      LetterGradeRange range = ranges.getRange(letterGrade);
      if (range == null) {
        continue;
      }
      LetterGradeRange range2 = ranges2.getRange(letterGrade);
      assertThat("Range for " + letterGrade, range2.minimum(), equalTo(range.minimum()));
    }
  }

  private void morphLetterGradeRanges(LetterGradeRanges ranges, int morphValue) {
    for (LetterGrade letterGrade : LetterGrade.values()) {
      LetterGradeRange range = ranges.getRange(letterGrade);
      if (range == null) {
        continue;
      }
      range.setRange(range.minimum() + morphValue, range.maximum() + morphValue);
    }
    LetterGradeRange fRange = ranges.getRange(LetterGrade.F);
    fRange.setRange(0, fRange.maximum());
  }

  private GradeBook writeAndReadGradeBookAsXml(GradeBook book) throws IOException, TransformerException, ParserException {
    Document doc = XmlDumper.dumpGradeBook(book, new XmlHelper());
    byte[] bytes = XmlHelper.getBytesForXmlDocument(doc);

//    System.out.println(new String(bytes));

    XmlGradeBookParser parser = new XmlGradeBookParser(new ByteArrayInputStream(bytes));
    return parser.parse();
  }

  @Test
  public void dueDatesArePersistedToXml() throws TransformerException, IOException, ParserException {
    GradeBook book = new GradeBook("test");
    String assignmentName = "assignment";
    Assignment assignment = new Assignment(assignmentName, 10.0);
    book.addAssignment(assignment);

    LocalDateTime dueDate = LocalDateTime.now().minusDays(3).withNano(0);
    assignment.setDueDate(dueDate);

    GradeBook book2 = writeAndReadGradeBookAsXml(book);

    assertThat(book2.getAssignment(assignmentName).getDueDate(), equalTo(dueDate));
  }

  @Test
  public void poaAssignmentsArePersistedToXml() throws TransformerException, IOException, ParserException {
    GradeBook book = new GradeBook("test");
    String assignmentName = "assignment";
    Assignment assignment = new Assignment(assignmentName, 10.0);
    assignment.setType(Assignment.AssignmentType.POA);
    book.addAssignment(assignment);

    GradeBook book2 = writeAndReadGradeBookAsXml(book);

    assertThat(book2.getAssignment(assignmentName).getType(), equalTo(Assignment.AssignmentType.POA));
  }

  @Test
  public void xmlHasNewlinesButNotIndentation() throws TransformerException, IOException {
    GradeBook book = new GradeBook("test");
    Document doc = XmlDumper.dumpGradeBook(book, new XmlHelper());;
    byte[] bytes = XmlHelper.getBytesForXmlDocument(doc);
    String string = new String(bytes);
    assertThat(string, containsString("\n"));
    assertThat(string.chars().filter(c -> '\n' == c).count(), greaterThan(4L));
    assertThat(string, not(containsString(" <")));
  }

  @Test
  void sectionNamesArePersistedToXml() throws ParserException, IOException, TransformerException {
    String undergraduate = "Undergraduate Section";
    String graduate = "Graduate Section";
    GradeBook book = new GradeBook("test");
    book.setSectionName(Student.Section.UNDERGRADUATE, undergraduate);
    book.setSectionName(Student.Section.GRADUATE, graduate);

    GradeBook book2 = writeAndReadGradeBookAsXml(book);

    assertThat(book2.getSectionName(Student.Section.UNDERGRADUATE), equalTo(undergraduate));
    assertThat(book2.getSectionName(Student.Section.GRADUATE), equalTo(graduate));
  }

  @Test
  void assignmentCanvasIdsArePersistedToXml() throws ParserException, IOException, TransformerException {
    String name = "Assignment";
    double points = 1.34;
    int canvasId = 12345;

    GradeBook book = new GradeBook("test");
    book.addAssignment(new Assignment(name, points).setCanvasId(canvasId));

    GradeBook book2 = writeAndReadGradeBookAsXml(book);
    Assignment assignment = book2.getAssignment(name);

    assertThat(assignment.getName(), equalTo(name));
    assertThat(assignment.getPoints(), equalTo(points));
    assertThat(assignment.getCanvasId(), equalTo(canvasId));
  }

  @Test
  void canParseGradebookWithoutAssignmentCanvasIds() throws ParserException {
    InputStream stream = getClass().getResourceAsStream("gradebookWithoutAssignmentCanvasIds.xml");
    GradeBook book = new XmlGradeBookParser(stream).parse();
    assertThat(book, notNullValue());
  }

  @Test
  void projectTypesArePersistedToXml() throws ParserException, IOException, TransformerException {
    Assignment.ProjectType projectType = APP_CLASSES;
    persistProjectOfType(projectType);
  }

  private void persistProjectOfType(Assignment.ProjectType projectType) throws IOException, TransformerException, ParserException {
    GradeBook book = new GradeBook("test");
    String projectName = "appClasses";
    Assignment appClasses = new Assignment(projectName, 1.0).setProjectType(projectType);
    book.addAssignment(appClasses);

    GradeBook book2 = writeAndReadGradeBookAsXml(book);

    assertThat(book2.getAssignment(projectName).getProjectType(), equalTo(projectType));
  }

  @Test
  void textFileProjectTypeIsPersistedToXml() throws ParserException, IOException, TransformerException {
    persistProjectOfType(TEXT_FILE);
  }

  @Test
  void prettyPrintProjectTypeIsPersistedToXml() throws ParserException, IOException, TransformerException {
    persistProjectOfType(PRETTY_PRINT);
  }

  @Test
  void xmlProjectTypeIsPersistedToXml() throws ParserException, IOException, TransformerException {
    persistProjectOfType(XML);
  }

  @Test
  void restProjectTypeIsPersistedToXml() throws ParserException, IOException, TransformerException {
    persistProjectOfType(REST);
  }

  @Test
  void androidProjectTypeIsPersistedToXml() throws ParserException, IOException, TransformerException {
    persistProjectOfType(ANDROID);
  }

  @Test
  void canParseGradeBookWithoutProjectType() throws ParserException {
    InputStream stream = getClass().getResourceAsStream("gradebookWithoutProjectTypes.xml");
    GradeBook book = new XmlGradeBookParser(stream).parse();
    assertThat(book, notNullValue());
  }

}
