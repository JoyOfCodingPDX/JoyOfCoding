package edu.pdx.cs.joy.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class represents a phone book that contains entries for
 * businesses and residents.  It is constructed from an XML DOM tree.
 * If we doing this for real, we'd want to be able to construct an
 * empty <code>PhoneBook</code> and add entries.  But, come on, this
 * is just an example.
 */
public class PhoneBook {
  private static PrintStream err = System.err;

  private Collection<PhoneBookEntry> entries = new ArrayList<>();

  /**
   * Creates a <code>PhoneBook</code> from an XML DOM tree.
   */
  public PhoneBook(Element root) {
    // Verify that this is a phonebook XML doc
    if (!root.getNodeName().equals("phonebook")) {
      String s = "Not a phonebook: " + root.getNodeName();
      throw new IllegalArgumentException(s);
    }

    NodeList entries = root.getChildNodes();
    for (int i = 0; i < entries.getLength(); i++) {
      Node node = entries.item(i);

      if (!(node instanceof Element)) {
        // Ignore other stuff
        continue;
      }

      Element entry = (Element) node;
      switch (entry.getNodeName()) {
        case "resident":
          this.entries.add(new Resident(entry));
          break;
        case "business":
          this.entries.add(new Business(entry));
          break;
        default:
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
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(true);

      DocumentBuilder builder =  factory.newDocumentBuilder();
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
    StringBuilder sb = new StringBuilder();
    sb.append("Phone Book\n\n");

    for (PhoneBookEntry entry : this.entries) {
      sb.append(entry);
      sb.append("\n");
    }
    
    return sb.toString();
  }
}
