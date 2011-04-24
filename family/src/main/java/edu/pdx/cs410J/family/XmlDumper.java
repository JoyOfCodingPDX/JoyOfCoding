package edu.pdx.cs410J.family;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

/**
 * This class dumps a family tree to a destination (for example, a
 * file) in XML format.  This file is meant to be used by an
 * <code>XmlParser</code> to create a <code>FamilyTree</code>.
 */
public class XmlDumper extends XmlHelper implements Dumper {
  private static PrintWriter err = new PrintWriter(System.err, true);

  private PrintWriter pw;      // Dumping destination

  /**
   * Creates a new XML dumper that dumps to a file of a given name.
   * If the file does not exist, it is created.
   */
  public XmlDumper(String fileName) throws IOException {
    this(new File(fileName));
  }

  /**
   * Creates a new XML dumper that dumps to a given file.
   */
  public XmlDumper(File file) throws IOException {
    this(new PrintWriter(new FileWriter(file), true));
  }

  /**
   * Creates a new XML dumper that prints to a
   * <code>PrintWriter</code>.  This way, we can dump to destinations
   * other than files.
   */
  public XmlDumper(PrintWriter pw) {
    this.pw = pw;
  }

  /**
   * Given a <code>Date</code> returns a chunk of a DOM tree
   * representing that date.
   */
  private static Element getDateElement(Document doc, Date date) {
    // Have to do all sorts of yucky Calendar stuff
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);

    // Make a date Element
    Element d = doc.createElement("date");
    
    int month = cal.get(Calendar.MONTH);
    Element m = doc.createElement("month");
    d.appendChild(m);
    m.appendChild(doc.createTextNode(String.valueOf(month)));

    int day = cal.get(Calendar.DATE);
    Element dy = doc.createElement("day");
    d.appendChild(dy);
    dy.appendChild(doc.createTextNode(String.valueOf(day)));

    int year = cal.get(Calendar.YEAR);
    Element y = doc.createElement("year");
    d.appendChild(y);
    y.appendChild(doc.createTextNode(String.valueOf(year)));

    // Return the date Element
    return d;
  }

  /**
   * Dumps the contents of a family tree to the desired destination.
   *
   * @throws FamilyTreeException
   *         An error occurred while dumping the family tree
   */
  public void dump(FamilyTree tree) {
    // First we create a DOM tree that represents the family tree
    Document doc = null;

    try {
      DocumentBuilderFactory factory =
        DocumentBuilderFactory.newInstance();
      factory.setValidating(true);

      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setErrorHandler(this);
      builder.setEntityResolver(this);

      DOMImplementation dom =
        builder.getDOMImplementation();
      DocumentType docType = 
        dom.createDocumentType("family-tree", publicID, systemID);
      doc = dom.createDocument(null, "family-tree", docType);

    } catch (ParserConfigurationException ex) {
      String s = "Illconfigured XML parser";
      throw new FamilyTreeException(s, ex);

    } catch (DOMException ex) {
      String s = "While creating XML Document";
      throw new FamilyTreeException(s, ex);
    }
    
    // Keep track of all of the marriages
    Set<Marriage> marriages = new HashSet<Marriage>();

    // Construct the DOM tree
    try {
      // Make the family tree
      Element ft = doc.getDocumentElement();

      // Make the people
      Iterator people = tree.getPeople().iterator();
      while (people.hasNext()) {
	Person person = (Person) people.next();
	
	// Create the person element
	Element p = doc.createElement("person");
        p.setAttribute("id", Integer.toString(person.getId()));
        p.setAttribute("gender", 
                       (person.getGender() == Person.MALE ? "male" :
                        "female"));
	ft.appendChild(p);

	String firstName = person.getFirstName();
	if(firstName != null) {
	  Element fn = doc.createElement("first-name");
	  p.appendChild(fn);
	  fn.appendChild(doc.createTextNode(firstName));
	}

	String middleName = person.getMiddleName();
	if(middleName != null) {
	  Element mn = doc.createElement("middle-name");
	  p.appendChild(mn);
	  mn.appendChild(doc.createTextNode(middleName));
	}

	String lastName = person.getLastName();
	if(lastName != null) {
	  Element ln = doc.createElement("last-name");
	  p.appendChild(ln);
	  ln.appendChild(doc.createTextNode(lastName));
	}

	Date dob = person.getDateOfBirth();
	if(dob != null) {
	  Element d = doc.createElement("dob");
	  p.appendChild(d);
	  d.appendChild(getDateElement(doc, dob));
	}

	Date dod = person.getDateOfDeath();
	if(dod != null) {
	  Element d = doc.createElement("dod");
	  p.appendChild(d);
	  d.appendChild(getDateElement(doc, dod));
	}

	Person father = person.getFather();
	if(father != null) {
	  Element f = doc.createElement("father-id");
	  p.appendChild(f);
	  f.appendChild(doc.createTextNode(String.valueOf(father.getId())));
	}

	Person mother = person.getMother();
	if(mother != null) {
	  Element m = doc.createElement("mother-id");
	  p.appendChild(m);
	  m.appendChild(doc.createTextNode(String.valueOf(mother.getId())));
	}

	// Make note of all marriages
	marriages.addAll(person.getMarriages());
	
      }

      // Make the marriages
      Iterator iter = marriages.iterator();
      while (iter.hasNext()) {
	Marriage marriage = (Marriage) iter.next();
	
	Element m = doc.createElement("marriage");
	m.setAttribute("husband-id",
                       String.valueOf(marriage.getHusband().getId()));
	m.setAttribute("wife-id",
                       String.valueOf(marriage.getWife().getId()));
	ft.appendChild(m);
	
	Date date = marriage.getDate();
	if(date != null) {
	  m.appendChild(getDateElement(doc, date));
	}

	String location = marriage.getLocation();
	if(location != null) {
	  Element l = doc.createElement("location");
	  m.appendChild(l);
	  l.appendChild(doc.createTextNode(location));
	}

      }

    } catch (DOMException ex) {
      String s = "** Exception while building DOM tree";
      throw new FamilyTreeException(s, ex);
    }

    // Then we simply write the DOM tree to the destination
    try {
      Source src = new DOMSource(doc);
      Result res = new StreamResult(this.pw);

      TransformerFactory xFactory = TransformerFactory.newInstance();
      Transformer xform = xFactory.newTransformer();
      xform.setOutputProperty(OutputKeys.INDENT, "yes");
      xform.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemID);
      xform.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, publicID);

      // Set stupid internal-xalan property to get a non-zero indent.
      // Or modify output_xml.properties in $JAVA_HOME/jre/lib/rt.jar
      xform.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

      // Suppress warnings about "Declared encoding not matching
      // actual one
      xform.setOutputProperty(OutputKeys.ENCODING, "ASCII");
      xform.transform(src, res);

    } catch (TransformerException ex) {
      String s = "While transforming XML";
      throw new FamilyTreeException(s, ex);
    }

    this.pw.flush();
  }
  

  /**
   * Test program.  Create a simple family tree and dump it to the
   * specified file or standard out if no file is specified.
   */
  public static void main(String[] args) {
    // Make a family tree and dump it

    // Make some people
    Person me = PersonMain.me();
    Person mom = PersonMain.mom(me);
    Person dad = PersonMain.dad(me);

    me.setMother(mom);
    me.setFather(dad);

    Marriage marriage = new Marriage(dad, mom);
    marriage.setLocation("Durham, NH");

    try {
      DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
      marriage.setDate(df.parse("Jul 12, 1969"));

    } catch (ParseException ex) {
      System.out.println("** Malformed marriage date?");
      System.exit(1);
    }

    mom.addMarriage(marriage);
    dad.addMarriage(marriage);

    // Create a family tree.  Add people in an interesting order.
    FamilyTree tree = new FamilyTree();
    tree.addPerson(dad);
    tree.addPerson(mom);
    tree.addPerson(me);

    // Dump the family tree 
    XmlDumper dumper = null;
    if (args.length > 0) {
      try {
	dumper = new XmlDumper(args[0]);

      } catch (IOException ex) {
	err.println("** IOException: " + ex);
	System.exit(1);
      }

    } else {
      PrintWriter out = new PrintWriter(System.out, true);
      dumper = new XmlDumper(out);
    }

    dumper.dump(tree);
  }
}
