package edu.pdx.cs410J.family;

import java.io.*;
import java.rmi.*;
import java.text.*;
import java.util.*;

/**
 * This program is an RMI client that adds a person to a remote family
 * tree
 */
public class UpdatePerson {
  private static PrintStream err = System.err;
  private static PrintStream out = System.out;

  /**
   * Prints usage information about this program
   */
  private static void usage(String s) {
    err.println("\n** " + s + "\n");
    err.println("usage: java UpdatePerson familyName host port " +
                "gender firstName lastName");
    err.println("  -middleName middleName");
    err.println("  -mother motherId");
    err.println("  -father fatherId");
    err.println("  -dob Date      The peron's date of birth");
    err.println("  -dod Date      The peron's date of death");
    err.println("");
    err.println("This program updates the information about a person "
                + "in a remote family tree.");
    err.println("If the person does not already exist in the tree, a "
                + "new person will be created.");
    err.println("");
    err.println("Dates should be in the form MM/DD/YYYY");
    err.println("");
    System.exit(1);
  }

  public static void main(String[] args) {
    String familyName = null;
    String host = null;
    int port = -1;
    Person.Gender gender = null;
    String firstName = null;
    String lastName = null;
    String middleName = null;
    int motherId = -1;
    int fatherId = -1;
    Date dob = null;
    Date dod = null;

    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-middleName")) {
        if (++i >= args.length) {
          usage("Missing middle name");
        }

        middleName = args[i];

      } else if (args[i].equals("-mother")) {
        if (++i >= args.length) {
          usage("Missing mother id");
        }

        try {
          motherId = Integer.parseInt(args[i]);

        } catch (NumberFormatException ex) {
          usage("Invalid mother id: " + args[i]);
        }

      } else if (args[i].equals("-father")) {
        if (++i >= args.length) {
          usage("Missing father id");
        }

        try {
          fatherId = Integer.parseInt(args[i]);

        } catch (NumberFormatException ex) {
          usage("Invalid father id: " + args[i]);
        }

      } else if (args[i].equals("-dob")) {
        if (++i >= args.length) {
          usage("Missing date of birth");
        }

        try {
          dob = df.parse(args[i]);
          
        } catch (ParseException ex) {
          usage("Malformed date: " + args[i]);
        }

      } else if (args[i].equals("-dod")) {
        if (++i >= args.length) {
          usage("Missing date of death");
        }

        try {
          dod = df.parse(args[i]);
          
        } catch (ParseException ex) {
          usage("Malformed date: " + args[i]);
        }

      } else if (familyName == null) {
        familyName = args[i];

      } else if (host == null) {
        host = args[i];

      } else if (port == -1) {
        try {
          port = Integer.parseInt(args[i]);

        } catch (NumberFormatException ex) {
          usage("Invalid port: " + args[i]);
        }

      } else if (gender == null) {
        if (args[i].equalsIgnoreCase("male")) {
          gender = Person.MALE;

        } else if (args[i].equalsIgnoreCase("female")) {
          gender = Person.FEMALE;

        } else {
          usage("Invalid gender: " + args[i]);
        }

      } else if (firstName == null) {
        firstName = args[i];

      } else if (lastName == null) {
        lastName = args[i];

      } else {
        usage("Spurious command line: " + args[i]);
      }
    }

    if (familyName == null) {
      usage("Missing family name");

    } else if (host == null) {
      usage("Missing host");

    } else if (port == -1) {
      usage("Missing port");

    } else if (gender == null) {
      usage("Missing gender");

    } else if (firstName == null) {
      usage("Missing first name");

    } else if (lastName == null) {
      usage("Missing last name");
    }

    // Install an RMISecurityManager, if there is not a
    // SecurityManager already installed
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new RMISecurityManager());
    }

    // Look up the remote family tree object
    String name = "rmi://" + host + ":" + port + "/" + familyName;

    try {
      RemoteFamilyTree tree = 
        (RemoteFamilyTree) Naming.lookup(name);

      RemotePerson person = tree.getPerson(firstName, lastName);
      if (person == null) {
        person = tree.createPerson(gender);
        person.setFirstName(firstName);
        person.setLastName(lastName);
      }

      if (middleName != null) {
        person.setMiddleName(middleName);
      }

      if (motherId != -1) {
        person.setMotherId(motherId);
      }

      if (fatherId != -1) {
        person.setFatherId(fatherId);
      }

      if (dob != null) {
        person.setDateOfBirth(dob);
      }

      if (dod != null) {
        person.setDateOfDeath(dod);
      }

      out.println("Updated: " + person.getDescription());

    } catch (Exception ex) {
      ex.printStackTrace(err);
    }

  }

}
