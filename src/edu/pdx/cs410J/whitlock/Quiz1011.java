package edu.pdx.cs410J.whitlock;

import java.io.*;
import java.util.*;

import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.grader.*;
import edu.pdx.cs410J.familyTree.*;

/**
 * This class implements the questions on the quiz given in class on
 * October 18, 2000.
 *
 * @author David Whitlock
 */
public class Quiz1011 {

  /**
   * Returns the full name of a person's maternal grandfather
   * (mother's father)
   */
  public static String getMaternalGrandfatherName(Person person) {
    return(person.getMother().getFather().getFullName());
  }

  /**
   * Returns the oldest living person in a family tree.  It is assumed
   * that all death dates are known.
   */
  public static Person getOldestLiving(FamilyTree tree) {
    Person oldest = null;
    Iterator people = tree.getPeople().iterator();
    while(people.hasNext()) {
      Person person = (Person) people.next();

      if(person.getDateOfBirth() == null) {
	// Something is wrong!
	continue;
      }

      if(person.getDateOfDeath() != null) {
	// Not living
	continue;
      }

      if(oldest == null) {
	oldest = person;
	continue;
      }

      if(person.getDateOfBirth().before(oldest.getDateOfBirth())) {
	// person was born before oldest, person is older 
	oldest = person;
      }
    }

    return(oldest);
  }

  /**
   * Returns the ``greatness'' of an ancestor relative to the person
   * with id 1.  For instance, your great-great-grandfather has a
   * ``greatness'' of 2.  Your parents and grandparents have
   * a ``greatness'' 0.
   */
  public static int getGreatness(FamilyTree tree, Person person) {
    // Use the person's id to determine greatness
    int id = person.getId();

    // Who remembers their high school algebra?
    double log = Math.log((double) id) / Math.log(2.0);
    int great = ((int) Math.floor(log)) - 2;

    if(great >= 1) {
      return(great);

    } else {
      return(0);
    }
  }

  private static PrintWriter err = new PrintWriter(System.err, true);
  private static PrintWriter out = new PrintWriter(System.out, true);

  private static final String PACKAGE = "edu.pdx.cs410J.";
  private static final String QUIZ = ".Quiz1011";

  /**
   * Displays information about how this program is used.
   */
  private static void usage() {
    err.println("usage: java Quiz1011 familyTreeFile gradeBookFile");
    System.exit(1);
  }

  /**
   * Test program that grades all of your submissions.
   */
  public static void main(String[] args) {
    String familyTreeFileName = null;
    String gradeBookFileName = null;
    Collection names = new ArrayList();  // Student's ids

    // Parse command line
    for(int i = 0; i < args.length; i++) {
      if(familyTreeFileName == null) {
	familyTreeFileName = args[i];

      } else if(gradeBookFileName == null) {
	gradeBookFileName = args[i];

      } else if(args[i].startsWith("-")) {
	err.println("** Unknown command line option: " + args[i]);
	usage();

      } else {
	names.add(args[i]);
      }
    }

    if(familyTreeFileName == null) {
      err.println("** Missing family tree file");
      usage();
    }

    if(gradeBookFileName == null) {
      err.println("** Missing grade book file");
      usage();
    }

    // Load the grade book
    GradeBook grades = new GradeBook(gradeBookFileName);

    // Make a family tree
    FamilyTree tree = null;
    try {
      tree = (new XmlParser(familyTreeFileName)).parse();

    } catch(FileNotFoundException ex) {
      err.println("** Could not find file: " + ex);
      System.exit(1);

    } catch(ParserException ex) {
      err.println("** Couldn't build family tree: " + ex);
      System.exit(1);
    }

    // Before we start using Tester...
    // Install a TesterSecurityManager that will allow us to run a
    // main method multiple times without exiting.
    TesterSecurityManager tsm = new TesterSecurityManager();
    System.setSecurityManager(tsm);

    File reportDir = new File(System.getProperty("user.dir"));
    String myClass = PACKAGE + "whitlock" + QUIZ;

    // Grade each student's submission
    Iterator ids = null;
    if(names.isEmpty()) {
      ids = grades.getUserIds().iterator();

    } else {
      ids = names.iterator();
    }

    while(ids.hasNext()) {
      String id = (String) ids.next();

      Tester tester = new Tester(id, reportDir);
      tester.printBanner("Quiz from November 18, 2000", '*');
    
      // Print out contents of family tree file
      tester.printBanner("Family tree source file", '-');
      tester.cat(new File("tree.xml"));

      Class[] paramTypes = { Person.class };
      Object[] params = { tree.getPerson(1) };
      Object result = 
	tester.executeStatic(PACKAGE + id + QUIZ, 
			     "getMaternalGrandfatherName",
			     paramTypes, params);
      tester.println("Test: Get maternal grandfather for " +
		     tree.getPerson(1).getFullName());
      Object expected =
 	tester.executeStatic(myClass, "getMaternalGrandfatherName",
			     paramTypes, params, false);
      tester.println("Expected result was: " + (String) expected);
      tester.println("Your result was: " + (String) result);
      tester.println("");

      paramTypes[0] = FamilyTree.class;
      params[0] = tree;
      result = tester.executeStatic(PACKAGE + id + QUIZ,
				    "getOldestLiving", paramTypes, params);
      expected = tester.executeStatic(myClass, "getOldestLiving", 
				      paramTypes, params, false);
      tester.println("Test: Get oldest living person in family tree");
      tester.println("Expected result was: " + 
		     ((Person) expected).getFullName());
      if(result == null) {
	tester.println("Your result was: " + result);

      } else {
	tester.println("Your result was: " + 
		       ((Person) result).getFullName());
      }
      tester.println("");
      
      Class[] paramTypes2 = { FamilyTree.class, Person.class };
      Object[] params2 = { tree, tree.getPerson(12) };
      result = tester.executeStatic(PACKAGE + id + QUIZ,
				    "getGreatness", paramTypes2,
				    params2);
      expected = tester.executeStatic(myClass, "getGreatness", 
				      paramTypes2, params2, false);
      tester.println("Test: Get the greatness of " + 
		     tree.getPerson(12).getFullName());
      tester.println("Expected result was: " + expected);
      tester.println("Your result was: " + result);
      tester.println("");
    }

    // Allow us to exit
    tsm.setAllowExit(true);
  }

}


