package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import static edu.pdx.cs410J.grader.GradeBook.LetterGradeRanges;
import static edu.pdx.cs410J.grader.GradeBook.LetterGradeRanges.LetterGradeRange;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
}
