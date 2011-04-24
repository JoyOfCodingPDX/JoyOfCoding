package edu.pdx.cs410J.xml;

import java.io.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This program parses and XML file and validates it for correctness
 * against its DTD.
 */
public class ValidateXml extends DefaultHandler {

  private static PrintStream err = System.err;

  public void error(SAXParseException ex) {
    err.println("ERROR at (" + ex.getLineNumber() + ":" +
                ex.getColumnNumber() + "): " + ex.getMessage());
  }

  public void fatalError(SAXParseException ex) {
    err.println("FATAL at (" + ex.getLineNumber() + ":" +
                ex.getColumnNumber() + "): " + ex.getMessage());
  }

  public void warning(SAXParseException ex) {
    err.println("WARNING at (" + ex.getLineNumber() + ":" +
                ex.getColumnNumber() + "): " + ex.getMessage());
  }

  public static void main(String[] args) {
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setValidating(true);

      SAXParser parser = factory.newSAXParser();
      parser.parse(new File(args[0]), new ValidateXml());

    } catch (ParserConfigurationException ex) {
      ex.printStackTrace(System.err);

    } catch (SAXException ex) {
      err.println("Parsing exception: " + ex);
      
    } catch (IOException ex) {
      err.println("IOException: " + ex);
    }
  }
}
