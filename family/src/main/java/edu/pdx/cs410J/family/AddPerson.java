package edu.pdx.cs410J.family;

import edu.pdx.cs410J.family.Person.Gender;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * This program demonstrates some of the functionality of out family
 * tree example by letting a user add people to a family tree that is
 * stored in a given file.
 *
 * @author David Whitlock
 * @since Fall 2000
 */
public class AddPerson {

  // Save you fingers from typing "System.out" all the time
  private static final PrintStream err = System.err;

  // Data for a person
  private static int id = 0;
  private static String gender;
  private static String fileName;
  private static boolean useXml = false;  // Text file by default
  private static String firstName;
  private static String middleName;
  private static String lastName;
  private static Date dob;          // Date of birth
  private static Date dod;          // Date of death
  private static int childId = 0;

  /**
   * Displays a help message on how to use this program.
   */
  private static void usage(String s) {
    err.println("\n** " + s + "\n");

    err.println("Adds a person to a family tree");
    err.println("usage: java AddPerson [options] <args>");
    err.println("  args are (in this order):");
    err.println("    file         File to store data in");
    err.println("    id           Person's id (greater or equal to 1)");
    err.println("    gender       Person's gender (male or female)");
    err.println("    firstName    Person's first name");
    err.println("    middleName   Person's middle name");
    err.println("    lastName     Person's last name");
    err.println("    dob          Person's date of birth " +
      "(e.g. Jun 27, 1936)");
    err.println("  options are (options may appear in any order):");
    err.println("    -parentOf id       Person's child");
    err.println("    -dod date          Person's date of death " +
      "(e.g. Jan 12, 1987)");
    err.println("    -xml               File in XML format instead of text");

    err.println("\n");
    System.exit(1);
  }

  /**
   * Parses the command line.
   *
   * @return An error message if the command line was not parsed
   *         sucessfully
   */
  private static String parseCommandLine(String[] args) {
    DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
    df.setLenient(false);

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-xml")) {
        useXml = true;

      } else if (args[i].equals("-dod")) {
        if (++i >= args.length) {
          return "Missing date of death";
        }

        // A date will take up three arguments
        StringBuffer sb = new StringBuffer();
        for (int j = 0; j < 3; j++) {
          if (i >= args.length) {
            return "Malformatted date of death: " + sb;

          } else {
            sb.append(args[i]).append(" ");
          }
          i++;
        }

        try {
          dod = df.parse(sb.toString().trim());

        } catch (ParseException ex) {
          return "Malformatted date of death: " + sb;
        }

      } else if (args[i].equals("-parentOf")) {
        if (++i >= args.length) {
          return "No child id specified";
        }

        try {
          childId = Integer.parseInt(args[i]);

        } catch (NumberFormatException ex) {
          return "Malformatted child id: " + args[i];
        }

        if (childId < 1) {
          return "Invalid child id value: " + childId;
        }

      } else if (fileName == null) {

        fileName = args[i];

      } else if (id == 0) {
        try {
          id = Integer.parseInt(args[i]);

        } catch (NumberFormatException ex) {
          return "Malformatted id: " + args[i];
        }

        if (id < 1) {
          return "Invalid id value: " + id;
        }

      } else if (gender == null) {

        gender = args[i];

      } else if (firstName == null) {

        firstName = args[i];

      } else if (middleName == null) {

        middleName = args[i];

      } else if (lastName == null) {

        lastName = args[i];

      } else if (dob == null) {
        // A date will take up three arguments
        StringBuffer sb = new StringBuffer();
        for (int j = 0; j < 3; j++) {
          if (i >= args.length) {
            return "Malformatted date of birth: " + sb;

          } else {
            sb.append(args[i]).append(" ");
          }

          i++;
        }

        try {
          dob = df.parse(sb.toString().trim());

        } catch (ParseException ex) {
          return "Malformatted date of birth: " + sb;
        }

      } else {
        return ("Unknown command line option: " + args[i]);
      }
    }

    // Make some additional checks
    if (id == 0) {
      return "No id specified";

    } else if (fileName == null) {
      return "No file name specified";

    } else if (gender == null) {
      return "Missing gender";

    } else if (firstName == null) {
      return "Missing first name";

    } else if (middleName == null) {
      return "Missing middle name";

    } else if (lastName == null) {
      return "Missing last name";
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
      usage(message);
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

      } catch (FamilyTreeException ex) {
        err.println("** File " + fileName + " is malformatted");
        System.exit(1);
      }

    } else {
      // No file to read, create a new family tree
      tree = new FamilyTree();
    }

    // Get a person and update its information
    Person person = tree.getPerson(id);

    if (person == null) {
      Gender g;

      if (gender == null) {
        String s = "Must specify a gender when creating a new person";
        err.println(s);
        System.exit(1);
        g = null;                 // Keep compiler from complaining

      } else if (gender.equalsIgnoreCase("male")) {
        g = Person.MALE;

      } else if (gender.equalsIgnoreCase("female")) {
        g = Person.FEMALE;

      } else {
        err.println("** Illegal gender: " + gender);
        System.exit(1);
        g = null;
      }

      person = new Person(id, g);
      tree.addPerson(person);
    }

    person.setFirstName(firstName);
    person.setMiddleName(middleName);
    person.setLastName(lastName);

    if (dob != null) {
      person.setDateOfBirth(dob);
    }

    if (dod != null) {
      person.setDateOfDeath(dod);
    }

    if (childId != 0) {
      // Make a child aware of his/her parent
      Person child = tree.getPerson(childId);
      if (child == null) {
        String s = "The child with id " + childId +
          " does not exist";
        err.println("\n** " + s + "\n");
        System.exit(1);
      }

      if (person.getGender() == Person.MALE) {
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
