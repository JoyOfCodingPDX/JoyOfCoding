package edu.pdx.cs410J.familyTree;

import edu.pdx.cs410J.ParserException;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * This program demonstrates some of the functionality of out family
 * tree example by letting a user add people to a family tree that is
 * stored in a given file.
 *
 * @author David Whitlock
 */
public class AddPerson {

  // Save you fingers from typing "System.out" all the time
  private static PrintWriter out = new PrintWriter(System.out, true);
  private static PrintWriter err = new PrintWriter(System.err, true);

  // Data for a person
  private static int id = -1;
  private static String fileName;
  private static boolean useXml = false;  // Text file by default
  private static String firstName;
  private static String middleName;
  private static String lastName;
  private static Date dob;          // Date of birth
  private static Date dod;          // Date of death
  private static int childId = -1;

  /**
   * Displays a help message on how to use this program.
   */
  private static void usage() {
    err.println("Adds a person to a family tree");
    err.println("usage: java AddPerson -id id -file file [options]");
    err.println("Where options are:");
    err.println("  -firstName name    Person's first name");
    err.println("  -middleName name   Person's middle name");
    err.println("  -lastname name     Person's last name");
    err.println("  -dob date          Person's date of birth " + 
		"(e.g. Jun 27, 1936)");
    err.println("  -dod date          Person's date of death " +
		"(e.g. Jan 12, 1987)");
    err.println("  -parentOf id       Person's child");
    err.println("  -text              File in text format (default)");
    err.println("  -xml               File in XML format");
  }

  /**
   * Parses the command line.
   *
   * @return An error message if the command line was not parsed
   *         sucessfully
   */
  private static String parseCommandLine(String[] args) {
    DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-id")) {
	// Next argument should be id
	if(++i >= args.length) {
	  // No more arguments to parse.  Eeep.
	  return "Missing id";
	}

	try {
	  id = Integer.parseInt(args[i]);
	
	} catch (NumberFormatException ex) {
	  return "Malformatted id: " + args[i];
	}
      
	if(id < 1) {
	  return "Invalid id value: " + id;
	}

      } else if (args[i].equals("-file")) {
	if(++i >= args.length) {
	  return "Missing file name";
	}

	fileName = args[i];

      } else if (args[i].equals("-text")) {
	useXml = false;

      } else if (args[i].equals("-xml")) {
	useXml = true;

      } else if (args[i].equals("-firstName")) {
	if(++i >= args.length) {
	  return "Missing first name";
	}

	firstName = args[i];

      } else if (args[i].equals("-middleName")) {
	if(++i >= args.length) {
	  return "Missing middle name";
	}

	middleName = args[i];

      } else if (args[i].equals("-lastName")) {
	if(++i >= args.length) {
	  return "Missing last name";
	}

	lastName = args[i];

      } else if (args[i].equals("-dob")) {
	if(++i >= args.length) {
	  return "Missing date of birth";
	}

	// A date will take up three arguments
	StringBuffer sb = new StringBuffer(args[i] + " ");
	for(int j = i+1; j < i+3; j++) {
	  if (j >= args.length) {
	    return "Malformatted date of birth: " + sb;

	  } else {
	    sb.append(args[j] + " ");
	  }
	}
	i = i+2;

	try {
	  dob = df.parse(sb.toString().trim());

	} catch (ParseException ex) {
	  return "Malformatted date of birth: " + sb;
	}

      } else if (args[i].equals("-dod")) {
	if(++i >= args.length) {
	  return "Missing date of death";
	}

	// A date will take up three arguments
	StringBuffer sb = new StringBuffer(args[i] + " ");
	for(int j = i+1; j < i+3; j++) {
	  if (j >= args.length) {
	    return "Malformatted date of birth: " + sb;

	  } else {
	    sb.append(args[j] + " ");
	  }
	}
	i = i+2;

	try {
	  dod = df.parse(sb.toString().trim());

	} catch (ParseException ex) {
	  return "Malformatted date of death: " + sb;
	}

      } else if (args[i].equals("-parentOf")) {
	if(++i >= args.length) {
	  return "No child id specified";
	}

	try {
	  childId = Integer.parseInt(args[i]);

	} catch (NumberFormatException ex) {
	  return "Malformatted child id: " + args[i];
	}

	if(childId < 1) {
	  return "Invalid child id value: " + childId;
	}

      } else {
	return("Unknown command line option: " + args[i]);
      }
    }

    // Make some additional checks
    if (id == -1) {
      return "No id specified";

    } else if (fileName == null) {
      return "No file name specified";
    }

    // No errors!
    return null;
  }

  /**
   * Main program that parsers the command line and adds a person to a
   * family tree.
   */
  public static void main(String[] args) {
    // First parse the command line
    String message = parseCommandLine(args);

    if (message != null) {
      // An error occurred, report it
      err.println("** " + message + "\n");
      usage();
      System.exit(1);
    }

    FamilyTree tree = null;

    // If the data file exists, read it in
    File file = new File(fileName);
    if (file.exists()) {
      Parser parser = null;
      if (useXml) {
	// File in XML format
	try {
	  parser = new XmlParser(file);

	} catch (FileNotFoundException ex) {
	  err.println("** Could not find file " + fileName);
	  System.exit(1);
	}
	
      } else {
	// File in text format
	try {
	  parser = new TextParser(file);

	} catch (FileNotFoundException ex) {
	  err.println("** Could not find file " + fileName);
	  System.exit(1);
	}
	
      }
      
      try {
	tree = parser.parse();

      } catch (ParserException ex) {
	err.println("** File " + fileName + " is malformatted");
	System.exit(1);
      }

    } else {
      // No file to read, create a new family tree
      tree = new FamilyTree();
    }

    // Get a person and update its information
    Person person = tree.getPerson(id);

    if (firstName != null) {
      person.setFirstName(firstName);
    }

    if (middleName != null) {
      person.setMiddleName(middleName);
    }

    if (lastName != null) {
      person.setLastName(lastName);
    }

    if (dob != null) {
      person.setDateOfBirth(dob);
    }

    if (dod != null) {
      person.setDateOfDeath(dod);
    }

    if (childId != -1) {
      // Make a child aware of his/her parent
      Person child = tree.getPerson(childId);
      
      // All males have even ids, all females have odd ids
      if (id % 2 == 0) {
	child.setFather(person);
	
      } else {
	child.setMother(person);
      }
    }

    // Now write the family tree to the file
    Dumper dumper = null;
    if (useXml) {
      try {
	dumper = new XmlDumper(file);

      } catch (IOException ex) {
	err.println("** Error while dealing with " + file);
	System.exit(1);
      }

    } else {
      try {
	dumper = new TextDumper(file);

      } catch (IOException ex) {
	err.println("** Error while dealing with " + file);
	System.exit(1);
      }
    }
    dumper.dump(tree);

  }

}
