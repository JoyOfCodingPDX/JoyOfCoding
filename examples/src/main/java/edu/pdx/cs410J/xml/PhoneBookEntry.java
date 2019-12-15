package edu.pdx.cs410J.xml;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an entry in a phone book.  It is used to
 * demonstrate how XML DOM trees can be turned into Java objects.
 */
public abstract class PhoneBookEntry {
  protected List<String> streetLines = new ArrayList<>();
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
      switch (element.getNodeName()) {
        case "street": {
          Node text = element.getFirstChild();
          this.streetLines.add(text.getNodeValue());
          break;
        }

        case "apt": {
          Node text = element.getFirstChild();
          this.apt = text.getNodeValue();
          break;
        }

        case "city": {
          Node text = element.getFirstChild();
          this.city = text.getNodeValue();
          break;
        }

        case "state": {
          Node text = element.getFirstChild();
          this.state = text.getNodeValue();
          break;
        }

        case "zip": {
          Node text = element.getFirstChild();
          this.zip = text.getNodeValue();
          break;
        }
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
      String nodeName = attr.getNodeName();
      switch (nodeName) {
        case "areacode":
          areacode = attr.getNodeValue();
          continue;

        case "number":
          number = attr.getNodeValue();
          continue;
      }
    }

    this.phone = areacode + "-" + number;
  }

  public String toString() {
    // Just make a string for the address and phone number
    StringBuilder sb = new StringBuilder();

    for (String line : this.streetLines) {
      sb.append(line);
      sb.append("\n");
    }

    if (apt != null) {
      sb.append("Apt ").append(this.apt).append("\n");
    }

    sb.append(this.city).append(", ").append(this.state).append(" ").append(this.zip).append("\n");
    sb.append(this.phone).append("\n");

    return sb.toString();
  }
}
