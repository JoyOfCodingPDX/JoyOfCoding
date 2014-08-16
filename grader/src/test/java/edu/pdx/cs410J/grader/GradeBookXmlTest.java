package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static edu.pdx.cs410J.grader.GradeBook.LetterGradeRanges;
import static edu.pdx.cs410J.grader.GradeBook.LetterGradeRanges.LetterGradeRange;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GradeBookXmlTest {

  @Test
  public void letterGradeRangesArePersistedToXml() throws IOException, TransformerException, ParserException {
    GradeBook book = new GradeBook("test");
    LetterGradeRanges ranges = book.getLetterGradeRanges();
    for (LetterGrade letterGrade : LetterGrade.values()) {
      LetterGradeRange range = ranges.getRange(letterGrade);
      if (range == null) {
        continue;
      }
      range.setRange(range.minimum() + 1, range.maximum() + 1);
    }
    LetterGradeRange fRange = ranges.getRange(LetterGrade.F);
    fRange.setRange(0, fRange.maximum());

    Document doc = XmlDumper.dumpGradeBook(book, new XmlHelper());
    byte[] bytes = XmlHelper.getBytesForXmlDocument(doc);

    XmlGradeBookParser parser = new XmlGradeBookParser(new ByteArrayInputStream(bytes));
    GradeBook book2 = parser.parse();

    LetterGradeRanges ranges2 = book2.getLetterGradeRanges();
    for (LetterGrade letterGrade : LetterGrade.values()) {
      LetterGradeRange range = ranges.getRange(letterGrade);
      if (range == null) {
        continue;
      }
      LetterGradeRange range2 = ranges2.getRange(letterGrade);
      assertThat("Range for " + letterGrade, range2.minimum(), equalTo(range.minimum()));
    }

  }
}
