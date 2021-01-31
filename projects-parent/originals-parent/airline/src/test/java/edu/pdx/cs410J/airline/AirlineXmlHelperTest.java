package edu.pdx.cs410J.airline;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class AirlineXmlHelperTest {

  @Test
  public void canParseValidXmlFile() throws ParserConfigurationException, IOException, SAXException {
    AirlineXmlHelper helper = new AirlineXmlHelper();


    DocumentBuilderFactory factory =
      DocumentBuilderFactory.newInstance();
    factory.setValidating(true);

    DocumentBuilder builder =
      factory.newDocumentBuilder();
    builder.setErrorHandler(helper);
    builder.setEntityResolver(helper);

    builder.parse(this.getClass().getResourceAsStream("valid-airline.xml"));
  }

  @Test(expected = SAXParseException.class)
  public void cantParseInvalidXmlFile() throws ParserConfigurationException, IOException, SAXException {
    AirlineXmlHelper helper = new AirlineXmlHelper();


    DocumentBuilderFactory factory =
      DocumentBuilderFactory.newInstance();
    factory.setValidating(true);

    DocumentBuilder builder =
      factory.newDocumentBuilder();
    builder.setErrorHandler(helper);
    builder.setEntityResolver(helper);

    builder.parse(this.getClass().getResourceAsStream("invalid-airline.xml"));
  }

}
