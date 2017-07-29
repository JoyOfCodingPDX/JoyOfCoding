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

    ProjectSubmission actual = convertToXmlAndBack(expected);
    assertThat(actual.getProjectName(), equalTo(projectName));
  }

  private ProjectSubmission convertToXmlAndBack(ProjectSubmission submission) throws JAXBException {
    ProjectSubmissionXmlConverter converter = new ProjectSubmissionXmlConverter();
    StringWriter sw = new StringWriter();
    converter.convertToXml(submission, sw);
    String xml = sw.toString();
    System.out.println(xml);
    return converter.convertFromXml(new StringReader(xml));
  }

  @Test
  public void convertStudentId() throws JAXBException {
    ProjectSubmission expected = new ProjectSubmission();
    String studentId = "studentId";
    expected.setStudentId(studentId);

    ProjectSubmission actual = convertToXmlAndBack(expected);
    assertThat(actual.getStudentId(), equalTo(studentId));
  }

}
