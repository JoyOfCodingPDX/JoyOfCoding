package edu.pdx.cs.joy.xml;

import org.w3c.dom.*;

/**
 * This class represents a business whose name, address and phone
 * number are listed in a phone book.  A <code>Business</code> is
 * constructed from an XML DOM tree.  If we were doing this for real,
 * we'd want a way of constructing an empty <code>Business</code> and
 * filling in its fields.
 */
public class Business extends PhoneBookEntry {
  protected String name;

  /**
   * Create a new <code>Business</code> from an <code>Element</code>
   * in a DOM tree.
   */
  public Business(Element root) {
    NodeList elements = root.getChildNodes();
    for (int i = 0; i < elements.getLength(); i++) {
      Node node = elements.item(i);

      if (!(node instanceof Element)) {
        continue;
      }

      Element element = (Element) node;

      switch (element.getNodeName()) {
        case "name":
          Node text = element.getFirstChild();
          this.name = text.getNodeValue();
          break;

        case "address":
          fillInAddress(element);
          break;

        case "phone":
          fillInPhone(element);
          break;

        default:
          String s = "Unknown element: " + element.getNodeName() + " (" +
            element.getNodeValue() + ")";
          throw new IllegalArgumentException(s);
      }
    }
  }

  public String toString() {
    return this.name + "\n" + super.toString();
  }
}
