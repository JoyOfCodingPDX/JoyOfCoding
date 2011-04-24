package edu.pdx.cs410J.family;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;


/**
 * This class tests the functionality of the <code>XmlDumper</code>
 * and <code>XmlParser</code> classes.
 */
public class XmlTest extends FamilyTreeConversionTestCase {

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
