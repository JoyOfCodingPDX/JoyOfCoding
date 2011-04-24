package edu.pdx.cs410J.family;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * This class dumps a family tree to a destination (for example, a
 * file) in a text-based format that is meant to be parsed by a
 * TextParser.  It is not necessarily human-readable.
 *
 * @see TextParser
 *
 * @author David Whitlock
 */
public class TextDumper implements Dumper {

  private PrintWriter pw;      // Dumping destination

  /**
   * Creates a new text dumper that dumps to a file of a given name.
   * If the file does not exist, it is created.
   */
  public TextDumper(String fileName) throws IOException {
    this(new File(fileName));
  }

  /**
   * Creates a new text dumper that dumps to a given file.
   */
  public TextDumper(File file) throws IOException {
    this(new PrintWriter(new FileWriter(file), true));
  }

  /**
   * Creates a new text dumper that prints to a
   * <code>PrintWriter</code>.  This way, we can dump to destinations
   * other than files.  
   */
  public TextDumper(PrintWriter pw) {
    this.pw = pw;
  }

  /**
   * Dumps the contents of a family tree to the desired desitination.
   */
  public void dump(FamilyTree tree) {
    // A word about the encoding of the family tree.  Each data item
    // (a person or a marriage) is preceeded by a header describing
    // the type of data and its size.  For instance,
    //          P 7
    // encodes a Person whose data takes up 7 lines in the file (not
    // including the header).
    //
    // Each attribute of a person is specified by an abbreviation of
    // the attribute followed by a string representation of the value.
    // For instance, a person's father is represented by 
    //              f: 4 
    // "f" for father and the father's id, 4.  
    //
    // Marriages are handled separately and are dumped to the file
    // after all of the people.

    // Keep track of all of the marriages we've encountered
    Set<Marriage> marriages = new HashSet<Marriage>();

    DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);

    // Examine every person in the family tree and dump their data to
    // the destination PrintWriter.
    Iterator iter = tree.getPeople().iterator();
    while (iter.hasNext()) {
      Person person = (Person) iter.next();

      int lines = 0;   // How many lines in the encoding
      StringBuffer data = new StringBuffer();

      data.append("id: " + person.getId() + "\n");
      lines++;

      data.append("g: " + person.getGender() + "\n");
      lines++;

      String firstName = person.getFirstName();
      if (firstName != null) {
	data.append("fn: " + firstName + "\n");
	lines++;
      }

      String middleName = person.getMiddleName();
      if (middleName != null) {
	data.append("mn: " + middleName + "\n");
	lines++;
      }

      String lastName = person.getLastName();
      if (lastName != null) {
	data.append("ln: " + lastName + "\n");
	lines++;
      }

      Person mother = person.getMother();
      if (mother != null) {
	data.append("m: " + mother.getId() + "\n");
	lines++;
      }

      Person father = person.getFather();
      if (father != null) {
	data.append("f: " + father.getId() + "\n");
	lines++;
      }

      Date dob = person.getDateOfBirth();
      if (dob != null) {
	data.append("dob: " + df.format(dob) + "\n");
	lines++;
      }

      Date dod = person.getDateOfDeath();
      if (dod != null) {
	data.append("dod: " + df.format(dod) + "\n");
	lines++;
      }

      // Make note of all marriages
      marriages.addAll(person.getMarriages());

      // Write the header followed by the data to the destination
      pw.println("P " + lines);
      pw.print(data.toString());
    }

    // Now we have to dump the marriages
    iter = marriages.iterator();
    while (iter.hasNext()) {
      Marriage marriage = (Marriage) iter.next();
      
      int lines = 0;
      StringBuffer data = new StringBuffer();

      // Write the id's of the husband and wife
      data.append(marriage.getHusband().getId() + " " +
		  marriage.getWife().getId() + "\n");
      lines++;

      String location = marriage.getLocation();
      if (location != null) {
	data.append("l: " + location + "\n");
	lines++;
      }

      Date date = marriage.getDate();
      if (date != null) {
	data.append("d: " + df.format(date) + "\n");
	lines++;
      }

      // Write the header followed by the data to the destination
      pw.println("M " + lines);
      pw.print(data.toString());
    }

    // Flush and close the PrintWriter
    pw.flush();
    pw.close();
  }

  /**
   * Test program.  Create a simple family tree and dump it to the
   * file specified by the first argument.
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println("** No file specified");
      System.exit(1);
    }

    String fileName = args[0];

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

    // Dump the family tree to the file
    try {
      TextDumper dumper = new TextDumper(fileName);
      dumper.dump(tree);

    } catch (IOException ex) {
      System.err.println("** IOException while dealing with " +
			 fileName);
      System.exit(1);
    }

  }

}
