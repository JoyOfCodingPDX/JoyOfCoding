package edu.pdx.cs410J.examples;

import java.io.*;
import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.*;

/**
 * This program parses and XML file and validates it for correctness
 * against its DTD.
 */
public class ValidateXml implements ErrorHandler {

  private static PrintStream err = System.err;

  public void error(SAXParseException ex) {
    err.println("ERROR: " + ex);
  }

  public void fatalError(SAXParseException ex) {
    err.println("FATAL: " + ex);
  }

  public void warning(SAXParseException ex) {
    err.println("WARNING: " + ex);
  }

  public static void main(String[] args) {
    DOMParser parser = null;
    try {
      InputSource source = new InputSource(new FileReader(args[0]));
      parser = new DOMParser();
      parser.setErrorHandler(new ValidateXml());

      try {
	// Validate the XML file against its DTD
        parser.setFeature("http://xml.org/sax/features/validation", true);
        
      } catch (SAXNotRecognizedException snr) {
      } catch (SAXNotSupportedException sns) {
      }
      
      parser.parse(source);

    } catch(SAXException ex) {
      err.println("Parsing exception: " + ex);
      
    } catch(IOException ex) {
      err.println("IOException: " + ex);
    }
  }
}
