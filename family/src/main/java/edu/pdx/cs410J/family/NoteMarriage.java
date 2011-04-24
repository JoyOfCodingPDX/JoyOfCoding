package edu.pdx.cs410J.family;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * This program makes note of a marriage between two people in a
 * family tree. 
 *
 * @author David Whitlock
 * @since Fall 2000
 */
public class NoteMarriage {

  private static PrintWriter err = new PrintWriter(System.err, true);

  private static int husbandId = 0;
  private static int wifeId = 0;
  private static Date date;
  private static String location;
  private static String fileName;
  private static boolean useXml = false;

  /**
   * Displays a help message on how to use this program.
   */
  private static void usage(String s) {
    err.println("\n** " + s + "\n");

    err.println("Makes note of a marriage between two people");
    err.println("usage: java NoteMarriage [options] <args>");
    err.println("  args are (in this order):");
    err.println("    file                File containing family info");
    err.println("    husbandId           The id of the husband");
    err.println("    wifeId              The id of the wife");

    err.println("  options are (options may appear in any order):");
    err.println("    -date date          Date marriage took place");
    err.println("    -location string    Where marriage took place");
    err.println("    -xml                File in XML format");

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

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-xml")) {
	useXml = true;

      } else if (args[i].equals("-date")) {
	if(++i >= args.length) {
	  return "Missing marriage date";
	}

	// A date will take up three arguments
	StringBuffer sb = new StringBuffer();
	for(int j = 0; j < 3; j++) {
	  if (i >= args.length) {
	    return "Malformatted date of birth: " + sb;

	  } else {
	    sb.append(args[i] + " ");
	  }

          i++;
	}
        i--;

	try {
	  date = df.parse(sb.toString().trim());

	} catch (ParseException ex) {
	  return "Malformatted marriage date: " + args[i];
	}

      } else if (args[i].equals("-location")) {
	if(++i >= args.length) {
	  return "Missing marriage location";
	}

	location = args[i];

      } else if (fileName == null) {
	fileName = args[i];

      } else if (husbandId == 0) {
	try {
	  husbandId = Integer.parseInt(args[i]);

	} catch (NumberFormatException ex) {
	  return "Malformatted husband id: " + args[i];
	}

	if (husbandId < 1) {
	  return "Illegal husband id value: " + husbandId;
	}

      } else if (wifeId == 0) {
	try {
	  wifeId = Integer.parseInt(args[i]);

	} catch (NumberFormatException ex) {
	  return "Malformatted wife id: " + args[i];
	}

	if(wifeId < 1) {
	  return "Illegal wife id value: " + husbandId;
	}

      } else {
	return("Unknown command line option: " + args[i]);
      }
    }

    // Make some additional checks
    if (husbandId == 0) {
      return "No husband id specified";

    } else if (wifeId == 0) {
      return "No wife id specified";

    } else if (fileName == null) {
      return "No file specified";
    }

    // No errors
    return null;
  }

  /**
   * Main program that parses the command line and creates a marriage.
   */
  public static void main(String[] args) {
    // Parse the command line
    String message = parseCommandLine(args);

    if (message != null) {
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

    // Get the husband the wife and set up the marriage
    Person husband = tree.getPerson(husbandId);
    Person wife = tree.getPerson(wifeId);

    Marriage marriage = new Marriage(husband, wife);
    husband.addMarriage(marriage);
    wife.addMarriage(marriage);

    if (location != null) {
      marriage.setLocation(location);
    }

    if (date != null) {
      marriage.setDate(date);
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
