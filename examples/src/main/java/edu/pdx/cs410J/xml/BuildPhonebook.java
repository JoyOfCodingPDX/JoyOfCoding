package edu.pdx.cs410J.xml;

import java.io.*;
import java.net.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

/**
 * This program builds a DOM tree that represents a phone book and
 * writes it to a file.
 */
public class BuildPhonebook {
  private static PrintStream err = System.err;

  public static void main(String[] args) {
    Document doc = null;

    String publicID = null;  // Who cares?
    String systemID = null;
    try {
      File dtd = new File("phonebook.dtd");
      systemID = dtd.toURL().toString();

    } catch (MalformedURLException ex) {
      err.println("** Bad URL: " + ex);
      System.exit(1);
    }

    // Create an empty Document
    try {
      DocumentBuilderFactory factory =
        DocumentBuilderFactory.newInstance();
      factory.setValidating(true);

      DocumentBuilder builder = factory.newDocumentBuilder();

      DOMImplementation dom =
        builder.getDOMImplementation();
      DocumentType docType = 
        dom.createDocumentType("phonebook", publicID, systemID);
      doc = dom.createDocument(null, "phonebook", docType);

    } catch (ParserConfigurationException ex) {
      ex.printStackTrace(System.err);
      System.exit(1);

    } catch (DOMException ex) {
      // Eep, this is bad
      ex.printStackTrace(System.err);
      System.exit(1);
    }
    
    // Construct the DOM tree
    try {
      Element root = doc.getDocumentElement();

      Element biz = doc.createElement("business");
      root.appendChild(biz);

      Element name = doc.createElement("name");
      biz.appendChild(name);
      String br = "CollegeNET, Inc.";
      name.appendChild(doc.createTextNode(br));
      
      Element address = doc.createElement("address");
      biz.appendChild(address);

      Element street1 = doc.createElement("street");
      address.appendChild(street1);
      String st1 = "805 SW Broadway";
      street1.appendChild(doc.createTextNode(st1));
      
      Element street2 = doc.createElement("street");
      address.appendChild(street2);
      String st2 = "Suite 1600";
      street2.appendChild(doc.createTextNode(st2));

      Element city = doc.createElement("city");
      address.appendChild(city);
      city.appendChild(doc.createTextNode("Portland"));

      Element state = doc.createElement("state");
      address.appendChild(state);
      state.appendChild(doc.createTextNode("OR"));

      Element zip = doc.createElement("zip");
      address.appendChild(zip);
      zip.appendChild(doc.createTextNode("97205"));

      Element phone = doc.createElement("phone");
      biz.appendChild(phone);
      phone.setAttribute("areacode", "503");
      phone.setAttribute("number", "973-5200");

    } catch (DOMException ex) {
      ex.printStackTrace(err);
      System.exit(1);
    }

    // Write the XML document to the console
    try {
      Source src = new DOMSource(doc);
      Result res = new StreamResult(System.out);

      TransformerFactory xFactory = TransformerFactory.newInstance();
      Transformer xform = xFactory.newTransformer();
      xform.setOutputProperty(OutputKeys.INDENT, "yes");
      xform.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemID);
      xform.transform(src, res);

    } catch (TransformerException ex) {
      ex.printStackTrace(System.err);
      System.exit(1);
    }
  }

}
