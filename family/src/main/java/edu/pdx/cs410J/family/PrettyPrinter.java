package edu.pdx.cs410J.family;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * This class dumps a family tree to a text file in a format that can
 * be read by a person.
 *
 * @author David Whitlock
 */
public class PrettyPrinter implements Dumper {

  private PrintWriter pw;     // Where to pretty print to

  /**
   * Creates a new pretty printer that prints to a file of a given
   * name.  If the file does not exist, it is created.
   */
  public PrettyPrinter(String fileName) throws IOException {
    this(new File(fileName));
  }

  /**
   * Creates a new pretty printer that prints to a given file.
   */
  public PrettyPrinter(File file) throws IOException {
    this(new PrintWriter(new FileWriter(file), true));
  }

  /**
   * Creates a new pretty printer that prints to a
   * <code>PrintWriter</code>.  This way, we can print to destinations
   * other than files (such as the console).
   */
  PrettyPrinter(PrintWriter pw) {
    this.pw = pw;
  }
  
  /**
   * Prints the contents of the given family tree in a human-readable
   * format.
   */
  public void dump(FamilyTree tree) {

    // We want to print the contents of the family tree sorted by id
    // number.  We'll use an instance of PersonSorter along with a
    // TreeSet to sort the Persons.
    SortedSet<Person> sortedTree = new TreeSet<Person>(new PersonSorter());
    sortedTree.addAll(tree.getPeople());

    // Print a banner
    pw.println("Your Family Tree\n");

    DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);

    // Iterate over the people in the sorted tree in order and print
    // out a description of each person.
    Iterator iter = sortedTree.iterator();
    while (iter.hasNext()) {
      Person person = (Person) iter.next();

      pw.println("Person " + person.getId() + ": " +
		 person.getFullName());

      Date dob = person.getDateOfBirth();
      if (dob != null) {
	pw.print("  Born on " + df.format(dob));
      }

      Date dod = person.getDateOfDeath();
      if (dod != null) {
	pw.print(", died on " + df.format(dod));
      }

      if (dob != null || dod != null) {
	// Only print a newline if a date was printed
	pw.println("");
      }

      Person mother = person.getMother();
      if (mother != null) {
	pw.println("  Mother: " + mother.getFullName() + " (" +
		   mother.getId() + ")");
      }

      Person father = person.getFather();
      if (father != null) {
	pw.println("  Father: " + father.getFullName() + " (" +
		   father.getId() + ")");
      }

      Iterator marriages = person.getMarriages().iterator();
      while (marriages.hasNext()) {
	Marriage marriage = (Marriage) marriages.next();
	Person spouse;

	if(marriage.getWife().equals(person)) {
	  spouse = marriage.getHusband();

	} else {
	  spouse = marriage.getWife();
	}

	pw.print("  Married " + spouse.getFullName());

	Date date = marriage.getDate();
	if(date != null) {
	  pw.print(" on " + df.format(date));
	}

	String location = marriage.getLocation();
	if(location != null) {
	  pw.print(" in " + location);
	}

	pw.println("");
      }

      // Put a blank line between people for readability
      pw.println("");

    }

    // Flush and close the PrintWriter for good measure
    pw.flush();
    pw.close();

  }

  /**
   * Inner class used only by the dump method.  It is used to sort
   * <code>Person</code>s by their id.
   */
  class PersonSorter implements Comparator<Person> {

    /**
     * Compares two <code>Person</code>s.  If the id of person one is less
     * than the id of person two, then person one is less than person
     * two.  If the id of person one is greater than the id of person
     * two, then person one greater than person two.  If the id of
     * person one is equal to the id of person two, then the two
     * persons are equal.
     */
    public int compare(Person p1, Person p2) {
      return p1.getId() - p2.getId();
    }

    /**
     * Compares one <code>PersonSorter</code> to another.  All
     * <code>PersonSorter</code>s are the same.
     */
    public boolean equals(Object o) {
      if (o instanceof PersonSorter) {
	return(true);

      } else {
	return(false);
      }
    }

  }

  /**
   * Test program.  Create a simple family tree and print it to the
   * console.
   */
  public static void main(String[] args) {
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

    // Pretty print the tree to the console
    PrintWriter out = new PrintWriter(System.out, true);
    PrettyPrinter pretty = new PrettyPrinter(out);
    pretty.dump(tree);
  }

}
