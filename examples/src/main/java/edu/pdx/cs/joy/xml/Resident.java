package edu.pdx.cs.joy.xml;

import org.w3c.dom.*;

/**
 * This class represents a resident (person) whose first and last
 * name, as well as whose middle initial, is listing in a phone book.
 * A <code>Resident</code> is constructed from an XML DOM tree.  If we
 * were doing this for real, we'd want a way of constructing an empty
 * <code>Resident</code> and filling in its fields.
 */
public class Resident extends PhoneBookEntry {
  private String firstName;
  private String middleInitial;
  private String lastName;
  private boolean unlisted = false;

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

      switch (element.getNodeName()) {
        case "first-name": {
          Node name = element.getFirstChild();
          Node text = name.getFirstChild();
          this.firstName = text.getNodeValue();
          break;
        }

        case "initial": {
          Node text = element.getFirstChild();
          this.middleInitial = text.getNodeValue();
          break;
        }

        case "last-name": {
          Node name = element.getFirstChild();
          Node text = name.getFirstChild();
          this.lastName = text.getNodeValue();
          break;
        }

        case "address":
          fillInAddress(element);
          break;

        case "phone":
          fillInPhone(element);
          break;
      }
    }

    // Check for the "unlisted" attribute
    if (root.getAttribute("unlisted").equals("true")) {
      this.unlisted = true;
    }
  }

  public String toString() {
    return this.firstName + " " + this.middleInitial + " " +
      this.lastName + "\n" +
      super.toString();
  }
}
