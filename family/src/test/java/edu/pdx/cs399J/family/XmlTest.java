package edu.pdx.cs399J.family;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This class tests the functionality of the <code>XmlDumper</code>
 * and <code>XmlParser</code> classes.
 */
public class XmlTest extends FamilyTreeConversionTestCase {

  /**
   * Returns a suite containing all of the tests in this class
   */
  public static Test suite() {
    return(new TestSuite(XmlTest.class));
  }

  /**
   * Creates a new <code>XmlTest</code> for running the test of a
   * given name
   */
  public XmlTest(String name) {
    super(name);
  }

  ////////  Helper Methods

  /**
   * Converts a FamilyTree to XML and returns the XML as a String.
   */
  protected String getStringFor(FamilyTree tree) {
    // Write the XML to a String
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    XmlDumper dumper = new XmlDumper(pw);
    dumper.dump(tree);
    String xml = sw.toString();
    return xml;
  }

  /**
   * Parsers a FamilyTree from a String containing XML
   */
  protected FamilyTree getFamilyTreeFor(String xml) 
    throws FamilyTreeException {

    // Parse the XML from the String
    StringReader sr = new StringReader(xml);
    XmlParser parser = new XmlParser(sr);
    return parser.parse();
  }


}
