package edu.pdx.cs410J.xml;

import org.w3c.dom.*;

/**
 * This class represents a resident (person) whose first and last
 * name, as well as whose middle initial, is listing in a phone book.
 * A <code>Resident</code> is constructed from an XML DOM tree.  If we
 * were doing this for real, we'd want a way of constructing an empty
 * <code>Resident</code> and filling in its fields.
 */
public class Resident extends PhoneBookEntry {
  protected String firstName;
  protected String middleInitial;
  protected String lastName;
  protected boolean unlisted = false;

  /**
   * Create a new <code>Resident</code> from a <code>Element</code> in
   * a DOM tree.
   */
  public Resident(Element root) {
    NodeList elements = root.getChildNodes();
    for (int i = 0; i < elements.getLength(); i++) {
      Node node = elements.item(i);

      if (!(node instanceof Element)) {
	continue;
      }

      Element element = (Element) node;

      if (element.getNodeName().equals("first-name")) {
	Node name = element.getFirstChild();
	Node text = name.getFirstChild();
	this.firstName = text.getNodeValue();

      } else if (element.getNodeName().equals("initial")) {
	Node text = element.getFirstChild();
	this.middleInitial = text.getNodeValue();

      } else if (element.getNodeName().equals("last-name")) {
	Node name = element.getFirstChild();
	Node text = name.getFirstChild();
	this.lastName = text.getNodeValue();

      } else if (element.getNodeName().equals("address")) {
	fillInAddress(element);

      } else if (element.getNodeName().equals("phone")) {
	fillInPhone(element);
      }
    }

    // Check for the "unlisted" attribute
    if (root.getAttribute("unlisted").equals("true")) {
      this.unlisted = true;
    }
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(this.firstName + " " + this.middleInitial + " " +
	      this.lastName + "\n");
    sb.append(super.toString());

    return sb.toString();
  }
}
