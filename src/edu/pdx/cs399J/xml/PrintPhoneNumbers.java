package edu.pdx.cs410J.xml;

import java.io.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This program demonstrates the SAX parsing API by parsing a
 * phonebook XML document and printing out the phone numbers in it.
 */
public class PrintPhoneNumbers extends DefaultHandler {
  private static PrintStream out = System.out;
  private static PrintStream err = System.err;

  /**
   * When we see a "phone" element, print out the area code and phone
   * number
   */
  public void startElement(String namespaceURI, String localName,
                           String qName, Attributes attrs)
    throws SAXException {

    if (qName.equals("phone")) {
      String area = attrs.getValue("areacode");
      String number = attrs.getValue("number");
      out.println("(" + area + ") " + number);
    }
  }

  public void warning(SAXParseException ex) {
    err.println("WARNING: " + ex);
  }

  public void error(SAXParseException ex) {
    err.println("ERROR: " + ex);
  }

  public void fatalError(SAXParseException ex) {
    err.println("FATAL: " + ex);
  }

  /**
   * Parses an XML file using SAX with an instance of this class used
   * for callbacks
   */
  public static void main(String[] args) {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(true);

    SAXParser parser = null;
    try {
      parser = factory.newSAXParser();

    } catch (ParserConfigurationException ex) {
      ex.printStackTrace(System.err);
      System.exit(1);

    } catch (SAXException ex) {
      ex.printStackTrace(System.err);
      System.exit(1);
    }

    DefaultHandler handler = new PrintPhoneNumbers();
    try {
      File file = new File(args[0]);

      InputSource source = new InputSource(new FileReader(file));
      source.setSystemId(file.toURL().toString());
      parser.parse(source, handler);

    } catch (SAXException ex) {
      ex.printStackTrace(System.err);
      System.exit(1);

    } catch (IOException ex) {
      ex.printStackTrace(System.err);
      System.exit(1);
    }
  }

}
