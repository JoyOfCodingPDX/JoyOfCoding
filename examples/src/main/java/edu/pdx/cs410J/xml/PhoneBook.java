package edu.pdx.cs410J.xml;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * This class represents a phone book that contains entries for
 * businesses and residents.  It is constructed from an XML DOM tree.
 * If we doing this for real, we'd want to be able to construct an
 * empty <code>PhoneBook</code> and add entries.  But, come on, this
 * is just an example.
 */
public class PhoneBook {
  private static PrintStream err = System.err;

  private Collection entries;

  /**
   * Creates a <code>PhoneBook</code> from an XML DOM tree.
   */
  public PhoneBook(Element root) {
    // Verify that this is a phonebook XML doc
    if (!root.getNodeName().equals("phonebook")) {
      String s = "Not a phonebook: " + root.getNodeName();
      throw new IllegalArgumentException(s);
    }

    this.entries = new ArrayList();
    NodeList entries = root.getChildNodes();
    for (int i = 0; i < entries.getLength(); i++) {
      Node node = entries.item(i);
      
      if (!(node instanceof Element)) {
	// Ignore other stuff
	continue;
      }

      Element entry = (Element) node;

      if (entry.getNodeName().equals("resident")) {
	Resident resident = new Resident(entry);
	this.entries.add(resident);

      } else if (entry.getNodeName().equals("business")) {
	Business business = new Business(entry);
	this.entries.add(business);

      } else {
	String s = "Unknown entry: " + entry.getNodeName() + " (" +
	  entry.getNodeValue() + ")";
	throw new IllegalArgumentException(s);
      }
    }
  }

  /**
   * Test program that takes the name of a XML file from the command
   * line and attempts to make a <code>PhoneBook</code> out of it.
   */
  public static void main(String[] args) {
    // Parse the XML file to create a DOM tree
    Document doc = null;
    try {
      DocumentBuilderFactory factory =
	DocumentBuilderFactory.newInstance();
      factory.setValidating(true);

      DocumentBuilder builder = 
	factory.newDocumentBuilder();
      doc = builder.parse(new File(args[0]));

    } catch (ParserConfigurationException ex) {
      err.println("** " + ex);
      System.exit(1);

    } catch (SAXException ex) {
      err.println("** SAXException: " + ex);
      System.exit(1);

    } catch (IOException ex) {
      err.println("** IOException: " + ex);
      System.exit(1);
    }

    Element root = (Element) doc.getChildNodes().item(1);
    PhoneBook phonebook = new PhoneBook(root);
    System.out.println(phonebook);
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("Phone Book\n\n");

    Iterator iter = this.entries.iterator();
    while (iter.hasNext()) {
      sb.append(iter.next());
      sb.append("\n");
    }
    
    return sb.toString();
  }
}
