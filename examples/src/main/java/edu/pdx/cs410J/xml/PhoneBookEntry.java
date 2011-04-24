package edu.pdx.cs410J.xml;

import java.util.*;

import org.w3c.dom.*;

/**
 * This class represents an entry in a phone book.  It is used to
 * demonstrate how XML DOM trees can be turned into Java objects.
 */
public abstract class PhoneBookEntry {
  protected List streetLines = new ArrayList();
  protected String apt;
  protected String city;
  protected String state;
  protected String zip;
  protected String phone;   // Includes area code XXX-XXX-XXXX

  /**
   * Helper method to fill in address data from a chunk of an XML DOM
   * tree. 
   */
  protected void fillInAddress(Element root) {
    NodeList elements = root.getChildNodes();
    for (int i = 0; i < elements.getLength(); i++) {
      Node node = elements.item(i);
      if (!(node instanceof Element)) {
	continue;
      }

      Element element = (Element) node;
      if (element.getNodeName().equals("street")) {
	Node text = element.getFirstChild();
	this.streetLines.add(text.getNodeValue());

      } else if (element.getNodeName().equals("apt")) {
	Node text = element.getFirstChild();
	this.apt = text.getNodeValue();

      } else if (element.getNodeName().equals("city")) {
	Node text = element.getFirstChild();
	this.city = text.getNodeValue();

      } else if (element.getNodeName().equals("state")) {
	Node text = element.getFirstChild();
	this.state = text.getNodeValue();

      } else if (element.getNodeName().equals("zip")) {
	Node text = element.getFirstChild();
	this.zip = text.getNodeValue();
      }
    }
  }

  /**
   * Helper method to fill in phone data from a chunk of an XML DOM
   * tree.
   */
  protected void fillInPhone(Element phone) {
    String areacode = null;
    String number = null;

    // Examine the phone's attributes
    NamedNodeMap attrs = phone.getAttributes();
    for (int i = 0; i < attrs.getLength(); i++) {
      Node attr = attrs.item(i);
      if (attr.getNodeName().equals("areacode")) {
	areacode = attr.getNodeValue();

      } else if (attr.getNodeName().equals("number")) {
	number = attr.getNodeValue();
      }
    }

    this.phone = areacode + "-" + number;
  }

  public String toString() {
    // Just make a string for the address and phone number
    StringBuffer sb = new StringBuffer();

    Iterator iter = this.streetLines.iterator();
    while (iter.hasNext()) {
      String line = (String) iter.next();
      sb.append(line);
      sb.append("\n");
    }

    if (apt != null) {
      sb.append("Apt " + this.apt + "\n");
    }

    sb.append(this.city + ", " + this.state + " " + this.zip + "\n");
    sb.append(this.phone + "\n");

    return sb.toString();
  }
}
