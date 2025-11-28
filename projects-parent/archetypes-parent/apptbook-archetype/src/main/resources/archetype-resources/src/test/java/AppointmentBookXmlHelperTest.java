#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AppointmentBookXmlHelperTest {

  @Test
  void canParseValidXmlFile() throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilder builder = newValidatingDocumentBuilder(new AppointmentBookXmlHelper());

    builder.parse(this.getClass().getResourceAsStream("valid-apptbook.xml"));
  }

  @Test
  void throwsExceptionWhenParsingInvalidXmlFile() throws ParserConfigurationException {
    DocumentBuilder builder = newValidatingDocumentBuilder(new AppointmentBookXmlHelper());

    assertThrows(SAXParseException.class, () ->
      builder.parse(this.getClass().getResourceAsStream("invalid-apptbook.xml"))
    );
  }

  private static DocumentBuilder newValidatingDocumentBuilder(AppointmentBookXmlHelper helper) throws ParserConfigurationException {
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
