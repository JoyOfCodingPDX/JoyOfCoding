package edu.pdx.cs410J.familyTree;

import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.*;

import com.sun.xml.tree.XmlDocument;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * This class dumps a family tree to a destination (for example, a
 * file) in XML format.  This file is meant to be used by an
 * <code>XmlParser</code> to create a <code>FamilyTree</code>.
 */
public class XmlDumper implements Dumper {
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
  private XmlDumper(PrintWriter pw) {
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
    m.appendChild(doc.createTextNode(month + ""));

    int day = cal.get(Calendar.DATE);
    Element dy = doc.createElement("day");
    d.appendChild(dy);
    dy.appendChild(doc.createTextNode(day + ""));

    int year = cal.get(Calendar.YEAR);
    Element y = doc.createElement("year");
    d.appendChild(y);
    y.appendChild(doc.createTextNode(year + ""));

    // Return the date Element
    return(d);
  }

  /**
   * Dumps the contents of a family tree to the desired destination.
   */
  public void dump(FamilyTree tree) {
    // First we create a DOM tree that represents the family tree
    DocumentBuilderFactory factory =
      DocumentBuilderFactory.newInstance();

    XmlDocument doc = null;
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      doc = (XmlDocument) builder.newDocument();

    } catch(ParserConfigurationException ex) {
      err.println("** Bad parser configuration: " + ex);
      System.exit(1);
    }

    // Specify the DTD
    try {
      String host = "www.cs.pdx.edu";
      String file = "/~whitlock/dtds/familytree.dtd";
      URL url = new URL("http", host, file);
      doc.setDoctype(null, url.toString(), null);

    } catch(MalformedURLException ex) {
      err.println("** Bad URL: " + ex);
      System.exit(1);
    }

    // Keep track of all of the marriages
    Set marriages = new HashSet();

    // Construct the DOM tree
    try {
      // Make the family tree
      Element ft = doc.createElement("familytree");
      doc.appendChild(ft);

      // Make the people
      Iterator people = tree.getPeople().iterator();
      while(people.hasNext()) {
	Person person = (Person) people.next();
	
	// Create the person element
	Element p = doc.createElement("person");
	ft.appendChild(p);

	Element id = doc.createElement("id");
	p.appendChild(id);
	id.appendChild(doc.createTextNode(person.getId() + ""));

	String firstName = person.getFirstName();
	if(firstName != null) {
	  Element fn = doc.createElement("firstname");
	  p.appendChild(fn);
	  fn.appendChild(doc.createTextNode(firstName));
	}

	String middleName = person.getMiddleName();
	if(middleName != null) {
	  Element mn = doc.createElement("middlename");
	  p.appendChild(mn);
	  mn.appendChild(doc.createTextNode(middleName));
	}

	String lastName = person.getLastName();
	if(lastName != null) {
	  Element ln = doc.createElement("lastname");
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
	  f.appendChild(doc.createTextNode(father.getId() + ""));
	}

	Person mother = person.getMother();
	if(mother != null) {
	  Element m = doc.createElement("mother-id");
	  p.appendChild(m);
	  m.appendChild(doc.createTextNode(mother.getId() + ""));
	}

	// Make note of all marriages
	marriages.addAll(person.getMarriages());
	
      }

      // Make the marriages
      Iterator iter = marriages.iterator();
      while(iter.hasNext()) {
	Marriage marriage = (Marriage) iter.next();
	
	Element m = doc.createElement("marriage");
	m.setAttribute("husband-id", marriage.getHusband().getId() + "");
	m.setAttribute("wife-id", marriage.getWife().getId() + "");
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

    } catch(DOMException ex) {
      err.println("** Exception while building DOM tree: " + ex);
      ex.printStackTrace(err);
      System.exit(1);
    }

    // Then we simply write the DOM tree to the destination
    try {
      doc.write(this.pw);

    } catch(IOException ex) {
      err.println("** IOException while writing XML: " + ex);
      System.exit(1);
    }
  }
  

  /**
   * Test program.  Create a simple family tree and dump it to the
   * specified file or standard out if no file is specified.
   */
  public static void main(String[] args) {
    // Make a family tree and dump it

    // Make some people
    Person me = Person.me();
    Person mom = Person.mom(me);
    Person dad = Person.dad(me);

    me.setMother(mom);
    me.setFather(dad);

    Marriage marriage = new Marriage(dad, mom);
    marriage.setLocation("Durham, NH");

    try {
      DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
      marriage.setDate(df.parse("Jul 12, 1969"));

    } catch(ParseException ex) {
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
    if(args.length > 0) {
      try {
	dumper = new XmlDumper(args[0]);

      } catch(IOException ex) {
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
