package edu.pdx.cs410J.examples;

import java.io.*;
import java.net.*;
import org.apache.xerces.dom.*;
import org.apache.xml.serialize.*;
import org.w3c.dom.*;

/**
 * This program builds a DOM tree that represents a phone book and
 * writes it to a file.
 *
 * <P align="center"><EM><A href =
 * "{@docRoot}/../src/edu/pdx/cs410J/examples/BuildPhonebook.java">
 * View Source</A></EM></P>
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
      DOMImplementation dom =
        DOMImplementationImpl.getDOMImplementation();
      DocumentType docType = 
        dom.createDocumentType("phonebook", publicID, systemID);
      doc = dom.createDocument(null, "phonebook", docType);

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
      String br = "Brokat Technologies";
      name.appendChild(doc.createTextNode(br));
      
      Element address = doc.createElement("address");
      biz.appendChild(address);

      Element street = doc.createElement("street");
      address.appendChild(street);
      String vn = "20575 NW von Neumann Drive";
      street.appendChild(doc.createTextNode(vn));
      
      Element city = doc.createElement("city");
      address.appendChild(city);
      city.appendChild(doc.createTextNode("Beaverton"));

      Element state = doc.createElement("state");
      address.appendChild(state);
      state.appendChild(doc.createTextNode("OR"));

      Element zip = doc.createElement("zip");
      address.appendChild(zip);
      zip.appendChild(doc.createTextNode("97006"));

      Element phone = doc.createElement("phone");
      biz.appendChild(phone);
      phone.setAttribute("areacode", "503");
      phone.setAttribute("number", "533-3000");

    } catch (DOMException ex) {
      ex.printStackTrace(err);
      System.exit(1);
    }

    // Write the XML document to the console
    try {
      OutputFormat format = new OutputFormat(doc);
      format.setIndenting(true);
      format.setIndent(2);
      format.setLineWidth(70);
    
      PrintWriter pw = new PrintWriter(System.out, true);
      XMLSerializer serial = new XMLSerializer(pw, format);
      serial.serialize(doc);

    } catch (IOException ex) {
      err.println("** IOException: " + ex);
      System.exit(1);
    }
  }

}
