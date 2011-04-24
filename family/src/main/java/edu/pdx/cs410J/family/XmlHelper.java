package edu.pdx.cs410J.family;

import java.io.*;
import java.net.URL;
import java.util.*;
import org.xml.sax.*;
import org.w3c.dom.*;

/**
 * This class contains fields and methods that are useful when dealing
 * with XML data.
 */
class XmlHelper implements EntityResolver, ErrorHandler {

  /** The System ID for the Family Tree DTD */
  protected static final String systemID = 
    "http://www.cs.pdx.edu/~whitlock/dtds/familytree.dtd";

  /** The Public ID for the Family Tree DTD */
  protected static final String publicID = 
    "-//Portland State University//DTD CS410J Family Tree//EN";

  ////////////////////  EntityResolver Methods  //////////////////

  /**
   * Attempt to resolve the external entity (such as a DTD) described
   * by the given public and system ID.  The external entity is
   * returned as a <code>InputSource</code>
   */
  public InputSource resolveEntity (String publicId, String systemId)
    throws SAXException, IOException {

    if (publicId != null && publicId.equals(XmlHelper.publicID)) {
      // We're resolving the external entity for the Family Tree's
      // DTD.  Check to see if its in the jar file.  This way we don't
      // need to go all the way to the website to find the DTD.
      String location = "/edu/pdx/cs410J/family/familytree.dtd";
      InputStream stream =
        this.getClass().getResourceAsStream(location);
      if (stream != null) {
        return new InputSource(stream);
      }
    }

    // Try to access the DTD using the URL
    try {
      URL url = new URL(systemId);
      InputStream stream = url.openStream();
      return new InputSource(stream);

    } catch (Exception ex) {
      return null;
    }
  }

  //////////////////  ErrorHandler Methods  ////////////////////////
  
  public void warning(SAXParseException ex) throws SAXException { 
    String s = "Warning while parsing XML (" + ex.getLineNumber() +
      ":" + ex.getColumnNumber() + "): " + ex.getMessage();
    System.err.println(s);
  }

  public void error(SAXParseException ex) throws SAXException {
    String s = "Error while parsing XML (" + ex.getLineNumber() +
      ":" + ex.getColumnNumber() + "): " + ex.getMessage();
    throw new SAXException(s);
  }
  
  public void fatalError(SAXParseException ex) throws SAXException {
    String s = "Fatal error while parsing XML (" + ex.getLineNumber()
      + ":" + ex.getColumnNumber() + "): " + ex.getMessage();
    throw new SAXException(s);
  }

  ///////////////////  Other Helper Methods  ///////////////////////

  /**
   * Extracts a bunch of notes from an <code>Element</code>
   */
  protected static List<String> extractNotesFrom(Element element) {
    List<String> list = new ArrayList<String>();

    NodeList children = element.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      if (!(node instanceof Element)) {
        continue;
      }

      Element child = (Element) node;
      if (child.getTagName().equals("note")) {
        list.add(extractTextFrom(child));
      }
    }

    return list;
  }

  /**
   * Extracts the text from an <code>Element</code>.
   */
  protected static String extractTextFrom(Element element) {
    Text text = (Text) element.getFirstChild();
    return (text == null ? "" : text.getData());
  }

}
