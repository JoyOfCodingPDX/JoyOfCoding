package edu.pdx.cs410J.grader.scoring;

import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ProjectSubmissionXmlConverterTest {

  @Test
  public void convertProjectName() throws JAXBException {
    ProjectSubmission expected = new ProjectSubmission();
    String projectName = "Project";
    expected.setProjectName(projectName);

    ProjectSubmissionXmlConverter converter = new ProjectSubmissionXmlConverter();
    StringWriter sw = new StringWriter();
    converter.convertToXml(expected, sw);
    String xml = sw.toString();
    System.out.println(xml);
    ProjectSubmission actual = converter.convertFromXml(new StringReader(xml));
    assertThat(actual.getProjectName(), equalTo(projectName));
  }

}
