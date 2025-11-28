package edu.pdx.cs.joy.phonebill;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PhoneBillXmlHelperTest {

  @Test
  void canParseValidXmlFile() throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilder builder = newValidatingDocumentBuilder(new PhoneBillXmlHelper());

    builder.parse(this.getClass().getResourceAsStream("valid-phonebill.xml"));
  }

  @Test
  void throwsExceptionWhenParsingInvalidXmlFile() throws ParserConfigurationException {
    DocumentBuilder builder = newValidatingDocumentBuilder(new PhoneBillXmlHelper());

    assertThrows(SAXParseException.class, () ->
      builder.parse(this.getClass().getResourceAsStream("invalid-phonebill.xml"))
    );
  }

  private static DocumentBuilder newValidatingDocumentBuilder(PhoneBillXmlHelper helper) throws ParserConfigurationException {
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
