package edu.pdx.cs399J.family;

import edu.pdx.cs399J.family.Person.Gender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class parses an XML file generated by <code>XmlDumper</code>
 * and creates a family tree.
 *
 * @author David Whitlock
 */
public class XmlParser extends XmlHelper implements Parser {

  private FamilyTree tree;  // The family tree we're building
  private Reader reader;    // Read XML file from here

  /**
   * Creates a new XML parser that reads its input from a file of a
   * given name.
   */
  public XmlParser(String fileName) throws FileNotFoundException {
    this(new File(fileName));
  }

  /**
   * Creates a new XML parser that reads its input from the given
   * file.
   */
  public XmlParser(File file) throws FileNotFoundException {
    this(new FileReader(file));
  }

  /**
   * Creates a new XML parser that reads itsinput from the given
   * <code>Reader</code>.  This lets us read from a source other
   * than a file.
   */
  public XmlParser(Reader reader) {
    this.reader = reader;
  }

  /**
   * Examines a chuck of a DOM tree and extracts a String from its
   * text.
   */
  private static String extractString(Node node) {
    return node.getFirstChild().getNodeValue();
  }

  /**
   * Examines a chunk of a DOM tree and extracts an int from its
   * text.
   */
  private static int extractInteger(Node node)
    throws FamilyTreeException {

    String text = extractString(node);
    try {
      return Integer.parseInt(text);

    } catch (NumberFormatException ex) {
      throw new FamilyTreeException("Bad integer: " + text);
    }
  }

  /**
   * Examines a chunk of a DOM tree and extracts a <code>Date</code>
   * from it.
   */
  private static Date extractDate(Element root)
    throws FamilyTreeException {

    // Make sure we're dealing with a data
    if (!root.getNodeName().equals("date")) {
      throw new FamilyTreeException("Not a <date>: " +
        root.getNodeName() + ", '" +
        root.getNodeValue() + "'");
    }

    Calendar cal = Calendar.getInstance();

    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      if (!(node instanceof Element)) {
        continue;
      }

      Element element = (Element) node;
      if (element.getNodeName().equals("month")) {
        cal.set(Calendar.MONTH, extractInteger(element));

      } else if (element.getNodeName().equals("day")) {
        cal.set(Calendar.DATE, extractInteger(element));

      } else if (element.getNodeName().equals("year")) {
        cal.set(Calendar.YEAR, extractInteger(element));

      } else {
        String s = "Invalidate element in date: " +
          element.getNodeName();
        throw new FamilyTreeException(s);
      }
    }

    return cal.getTime();
  }

  /**
   * Examines a chunk of a DOM tree and adds a person to the family
   * tree.
   */
  private void handlePerson(Element root) throws FamilyTreeException {
    // Make sure that we're dealing with a person here
    if (!root.getNodeName().equals("person")) {
      throw new FamilyTreeException("Expecting a <person>");
    }

    Person person = null;
    int id;
    try {
      id = Integer.parseInt(root.getAttribute("id"));

    } catch (NumberFormatException ex) {
      String s = "Person id \"" + root.getAttribute("id") +
        "\" is not a valid id";
      throw new FamilyTreeException(s);
    }

    Gender gender = (root.getAttribute("gender").equals("male")
                  ? Person.MALE : Person.FEMALE);
    person = this.tree.getPerson(id);
    if (person == null) {
      person = new Person(id, gender);
      this.tree.addPerson(person);

    } else {
      if (gender != person.getGender()) {
        String s = "Expecting " + person + " to be " +
          (gender == Person.MALE ? "MALE" : " FEMALE");
        throw new FamilyTreeException(s);
      }
    }

    NodeList elements = root.getChildNodes();
    for (int i = 0; i < elements.getLength(); i++) {
      Node node = elements.item(i);
      if (!(node instanceof Element)) {
        continue;
      }

      Element element = (Element) node;

      if (element.getNodeName().equals("first-name")) {
        person.setFirstName(extractString(element));

      } else if (element.getNodeName().equals("last-name")) {
        person.setLastName(extractString(element));

      } else if (element.getNodeName().equals("middle-name")) {
        person.setMiddleName(extractString(element));

      } else if (element.getNodeName().equals("dob")) {
        Element dob = null;

        NodeList list = element.getChildNodes();
        for (int j = 0; j < list.getLength(); j++) {
          Node n = list.item(j);
          if (n instanceof Element) {
            dob = (Element) n;
            break;
          }
        }

        if (dob == null) {
          throw new FamilyTreeException("No <date> in <dob>?");
        }

        person.setDateOfBirth(extractDate(dob));

      } else if (element.getNodeName().equals("dod")) {
        Element dod = null;

        NodeList list = element.getChildNodes();
        for (int j = 0; j < list.getLength(); j++) {
          Node n = list.item(j);
          if (n instanceof Element) {
            dod = (Element) n;
            break;
          }
        }

        if (dod == null) {
          throw new FamilyTreeException("No <date> in <dod>?");
        }

        person.setDateOfDeath(extractDate(dod));

      } else if (element.getNodeName().equals("father-id")) {
        String s = extractString(element);
        int fid = 0;
        try {
          fid = Integer.parseInt(s);

        } catch (NumberFormatException ex) {
          throw new FamilyTreeException("Bad father-id: " + s);
        }

        Person father = this.tree.getPerson(fid);
        if (father == null) {
          father = new Person(fid, Person.MALE);
          this.tree.addPerson(father);
        }
        person.setFather(father);

      } else if (element.getNodeName().equals("mother-id")) {
        String s = extractString(element);
        int mid = 0;
        try {
          mid = Integer.parseInt(s);

        } catch (NumberFormatException ex) {
          throw new FamilyTreeException("Bad mother-id: " + s);
        }

        Person mother = this.tree.getPerson(mid);
        if (mother == null) {
          mother = new Person(mid, Person.FEMALE);
          this.tree.addPerson(mother);
        }
        person.setMother(mother);
      }
    }
  }

  /**
   * Examines a chunk of a DOM tree and makes note of a marriage.
   */
  private void handleMarriage(Element root) throws FamilyTreeException {
    // Make sure we're dealing with a marriage
    if (!root.getNodeName().equals("marriage")) {
      throw new FamilyTreeException("");
    }

    int husband_id = 0;
    int wife_id = 0;

    // Extract the husband and wife id's
    NamedNodeMap attrs = root.getAttributes();
    for (int i = 0; i < attrs.getLength(); i++) {
      Node attr = attrs.item(i);
      if (attr.getNodeName().equals("husband-id")) {
        String id = attr.getNodeValue();

        try {
          husband_id = Integer.parseInt(id);

        } catch (NumberFormatException ex) {
          throw new FamilyTreeException("Bad husband id: " + id);
        }

      } else if (attr.getNodeName().equals("wife-id")) {
        String id = attr.getNodeValue();

        try {
          wife_id = Integer.parseInt(id);
        } catch (NumberFormatException ex) {
          throw new FamilyTreeException("Bad wife id: " + id);
        }
      }
    }

    // Make a Marriage
    Person husband = this.tree.getPerson(husband_id);
    Person wife = this.tree.getPerson(wife_id);

    Marriage marriage = new Marriage(husband, wife);
    husband.addMarriage(marriage);
    wife.addMarriage(marriage);

    // Fill in info about the marriage
    NodeList elements = root.getChildNodes();
    for (int i = 0; i < elements.getLength(); i++) {
      Node node = elements.item(i);
      if (!(node instanceof Element)) {
        continue;
      }

      Element element = (Element) node;
      if (element.getNodeName().equals("location")) {
        marriage.setLocation(extractString(element));

      } else if (element.getNodeName().equals("date")) {
        marriage.setDate(extractDate(element));
      }
    }
  }

  /**
   * Parses the specified input source in XML format and from it
   * creates a family tree.
   */
  public FamilyTree parse() throws FamilyTreeException {
    this.tree = new FamilyTree();

    // Create a DOM tree from the XML source
    Document doc = null;
    try {
      DocumentBuilderFactory factory =
        DocumentBuilderFactory.newInstance();
      factory.setValidating(true);

      DocumentBuilder builder =
        factory.newDocumentBuilder();
      builder.setErrorHandler(this);
      builder.setEntityResolver(this);

      doc = builder.parse(new InputSource(this.reader));

    } catch (ParserConfigurationException ex) {
      throw new FamilyTreeException("While parsing XML source: " + ex, ex);

    } catch (SAXException ex) {
      throw new FamilyTreeException("While parsing XML source: " + ex, ex);

    } catch (IOException ex) {
      throw new FamilyTreeException("While parsing XML source: " + ex, ex);
    }

    Element root = (Element) doc.getChildNodes().item(1);

    // Make sure that we are really dealing with a family tree
    if (!root.getNodeName().equals("family-tree")) {
      throw new FamilyTreeException("Not a family tree XML source: " +
        root.getNodeName());
    }

    NodeList stuff = root.getChildNodes();
    for (int i = 0; i < stuff.getLength(); i++) {
      Node node = stuff.item(i);

      if (!(node instanceof Element)) {
        // Ignore whitespace text and other stuff
        continue;
      }

      Element element = (Element) node;

      if (element.getNodeName().equals("person")) {
        handlePerson(element);

      } else if (element.getNodeName().equals("marriage")) {
        handleMarriage(element);

      } else {
        String s = "A family tree should not have a " +
          element.getNodeName();
        throw new FamilyTreeException(s);
      }
    }

    return this.tree;
  }

  /**
   * Test program.  Parses an XML file specified on the command line
   * and prints the resulting family tree to standard out.
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println("** Missing file name");
      System.exit(1);
    }

    // Parse the input file
    String fileName = args[0];
    try {
      Parser parser = new XmlParser(fileName);
      FamilyTree tree = parser.parse();

      PrintWriter out = new PrintWriter(System.out, true);
      PrettyPrinter pretty = new PrettyPrinter(out);
      pretty.dump(tree);

    } catch (FileNotFoundException ex) {
      System.err.println("** Could not find file " + fileName);

    } catch (FamilyTreeException ex) {
      ex.printStackTrace(System.err);
    }
  }

}
