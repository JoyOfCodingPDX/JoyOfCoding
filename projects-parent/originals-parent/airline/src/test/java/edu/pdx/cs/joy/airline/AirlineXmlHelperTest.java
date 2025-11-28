package edu.pdx.cs.joy.airline;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AirlineXmlHelperTest {

  @Test
  void canParseValidXmlFile() throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilder builder = newValidatingDocumentBuilder(new AirlineXmlHelper());

    builder.parse(this.getClass().getResourceAsStream("valid-airline.xml"));
  }

  @Test
  void throwsExceptionWhenParsingInvalidXmlFile() throws ParserConfigurationException {
    DocumentBuilder builder = newValidatingDocumentBuilder(new AirlineXmlHelper());

    assertThrows(SAXParseException.class, () ->
      builder.parse(this.getClass().getResourceAsStream("invalid-airline.xml"))
    );
  }

  private static DocumentBuilder newValidatingDocumentBuilder(AirlineXmlHelper helper) throws ParserConfigurationException {
    DocumentBuilderFactory factory =
      DocumentBuilderFactory.newInstance();
    factory.setValidating(true);

    DocumentBuilder builder =
      factory.newDocumentBuilder();
    builder.setErrorHandler(helper);
    builder.setEntityResolver(helper);
    return builder;
  }

}
