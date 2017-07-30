package edu.pdx.cs410J.grader.scoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProjectSubmissionXmlConverter {
  private final JAXBContext xmlContext;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public ProjectSubmissionXmlConverter() throws JAXBException {
    xmlContext = JAXBContext.newInstance(ProjectSubmission.class);
  }

  public void convertToXml(ProjectSubmission submission, Writer writer) throws JAXBException {
    Marshaller marshaller = this.xmlContext.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    marshaller.marshal(submission, writer);
  }

  public ProjectSubmission convertFromXml(Reader reader) throws JAXBException {
    Unmarshaller unmarshaller = this.xmlContext.createUnmarshaller();
    return (ProjectSubmission) unmarshaller.unmarshal(reader);
  }

  public static void main(String... args) throws JAXBException {
    List<String> outFileNames = new ArrayList<>();
    outFileNames.addAll(Arrays.asList(args));

    if (outFileNames.isEmpty()) {
      usage("Missing project submission out file name");
    }

    ProjectSubmissionXmlConverter converter = new ProjectSubmissionXmlConverter();
    outFileNames.stream().map(File::new).forEach(converter::convertOutFileToXml);
  }

  private void convertOutFileToXml(File outFile) {
    if (!outFile.exists()) {
      logger.error("Output file \"" + outFile + "\" does not exist");
      return;
    }

    ProjectSubmission submission;
    try {
      ProjectSubmissionOutputFileParser parser = new ProjectSubmissionOutputFileParser(new FileReader(outFile));
      submission = parser.parse();

    } catch (FileNotFoundException e) {
      logger.error("Could not file file " + outFile, e);
      return;
    }

    String outFileName = outFile.getName();
    String filePrefix= outFileName.substring(outFileName.indexOf('.'));
    String xmlFileName = filePrefix + ".xml";

    File directory = outFile.getParentFile();
    File xmlFile = new File(directory, xmlFileName);

    try {
      convertToXml(submission, new FileWriter(xmlFile));

    } catch (JAXBException | IOException e) {
      logger.error("While writing to " + xmlFile, e);
    }

  }

  private static void usage(String message) {
    PrintStream err = System.err;
    err.println("** " + message);
    err.println();
    err.println("usage: java ProjectSubmissionsXmlConverter outFileName+");
    err.println("  outFileName    A project submission output file");
    err.println();

    System.exit(1);
  }
}
