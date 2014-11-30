package edu.pdx.cs410J.grader;

import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains fields and methods that are useful when dealing
 * with XML data.
 */
class XmlHelper implements EntityResolver, ErrorHandler {

  /** The System ID for the Grade Book DTD */
  protected static final String systemID = 
    "http://www.cs.pdx.edu/~whitlock/dtds/gradebook.dtd";

  /** The Public ID for the Grade Bookd DTD */
  protected static final String publicID = 
    "-//Portland State University//DTD CS410J Grade Book//EN";

  protected static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  static byte[] getBytesForXmlDocument(Document xmlDoc) throws TransformerException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PrintWriter pw =
        new PrintWriter(new OutputStreamWriter(baos), true);
      writeXmlToPrintWriter(xmlDoc, pw);
      return baos.toByteArray();
    }

  ////////////////////  EntityResolver Methods  //////////////////

  /**
   * Attempt to resolve the external entity (such as a DTD) described
   * by the given public and system ID.  The external entity is
   * returned as a <code>InputSource</code>
   */
  @Override
  public InputSource resolveEntity (String publicId, String systemId)
    throws SAXException, IOException {

    if (publicId != null && publicId.equals(XmlHelper.publicID)) {
      // We're resolving the external entity for the Grade Book's
      // DTD.  Check to see if its in the jar file.  This way we don't
      // need to go all the way to the website to find the DTD.
      String location = "/edu/pdx/cs410J/grader/gradebook.dtd";
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
  
  @Override
  public void warning(SAXParseException ex) throws SAXException {
    // Most warnings are annoying

//      String s = "Warning while parsing XML (" + ex.getLineNumber() +
//        ":" + ex.getColumnNumber() + "): " + ex.getMessage();
//      System.err.println(s);
  }

  @Override
  public void error(SAXParseException ex) throws SAXException {
    String s = "Error while parsing XML (" + ex.getLineNumber() +
      ":" + ex.getColumnNumber() + "): " + ex.getMessage();
    throw new SAXException(s);
  }
  
  @Override
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
    List<String> list = new ArrayList<>();

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

  static void writeXmlToPrintWriter(Document doc, PrintWriter pw) throws TransformerException {
    Source src = new DOMSource(doc);
    Result res = new StreamResult(pw);

    TransformerFactory xFactory = TransformerFactory.newInstance();
    Transformer xform = xFactory.newTransformer();
    xform.setOutputProperty(OutputKeys.INDENT, "yes");
    xform.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemID);
    xform.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, publicID);

    // Suppress warnings about "Declared encoding not matching
    // actual one
    xform.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

    xform.transform(src, res);
  }
}
