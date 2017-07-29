package edu.pdx.cs410J.grader.scoring;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.Reader;
import java.io.Writer;

public class ProjectSubmissionXmlConverter {
  private final JAXBContext xmlContext;

  public ProjectSubmissionXmlConverter() throws JAXBException {
    xmlContext = JAXBContext.newInstance(ProjectSubmission.class);
  }

  public void convertToXml(ProjectSubmission submission, Writer writer) throws JAXBException {
    Marshaller marshaller = this.xmlContext.createMarshaller();
    marshaller.marshal(submission, writer);
  }

  public ProjectSubmission convertFromXml(Reader reader) throws JAXBException {
    Unmarshaller unmarshaller = this.xmlContext.createUnmarshaller();
    return (ProjectSubmission) unmarshaller.unmarshal(reader);
  }
}
