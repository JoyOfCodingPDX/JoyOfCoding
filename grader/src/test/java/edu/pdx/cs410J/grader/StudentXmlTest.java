package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class StudentXmlTest {

  @Test
  public void letterGradeIsPersistedInXml() throws TransformerException, ParserException {
    Student student = new Student("test");
    LetterGrade letterGrade = LetterGrade.A;
    student.setLetterGrade(letterGrade);

    Document doc = XmlDumper.toXml(student);
    byte[] bytes = XmlHelper.getBytesForXmlDocument(doc);

    XmlStudentParser parser = new XmlStudentParser(new InputStreamReader(new ByteArrayInputStream(bytes)));
    Student student2 = parser.parseStudent();

    assertThat(student2.getLetterGrade(), equalTo(letterGrade));
  }

}
