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

class PhoneBillXmlHelperTest {

  @Test
  void canParseValidXmlFile() throws ParserConfigurationException, IOException, SAXException {
    PhoneBillXmlHelper helper = new PhoneBillXmlHelper();


    DocumentBuilderFactory factory =
      DocumentBuilderFactory.newInstance();
    factory.setValidating(true);

    DocumentBuilder builder =
      factory.newDocumentBuilder();
    builder.setErrorHandler(helper);
    builder.setEntityResolver(helper);

    builder.parse(this.getClass().getResourceAsStream("valid-${artifactId}.xml"));
  }

  @Test
  void throwsExceptionWhenParsingInvalidXmlFile() throws ParserConfigurationException {
    PhoneBillXmlHelper helper = new PhoneBillXmlHelper();


    DocumentBuilderFactory factory =
      DocumentBuilderFactory.newInstance();
    factory.setValidating(true);

    DocumentBuilder builder =
      factory.newDocumentBuilder();
    builder.setErrorHandler(helper);
    builder.setEntityResolver(helper);

    assertThrows(SAXParseException.class, () ->
      builder.parse(this.getClass().getResourceAsStream("invalid-${artifactId}.xml"))
    );
  }

}
