package edu.pdx.cs410J.familyTree;

import edu.pdx.cs410J.ParserException;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * This program makes note of a marriage between two people in a
 * family tree. 
 *
 * @author David Whitlock
 */
public class NoteMarriage {

  // Save your fingers from typing "System.out" all the time
  private static PrintWriter out = new PrintWriter(System.out, true);
  private static PrintWriter err = new PrintWriter(System.err, true);

  private static int husbandId = -1;
  private static int wifeId = -1;
  private static Date date;
  private static String location;
  private static String fileName;
  private static boolean useXml = false;

  /**
   * Displays a help message on how to use this program.
   */
  private static void usage() {
    err.println("Makes note of a marriage between two people");
    err.println("usage: java NoteMarriage -husbandId id -wifeId id " +
		"-file file [options]");
    err.println("Where options are:");
    err.println("  -date date          Date marriage took place");
    err.println("  -location string    Where marriage took place");
    err.println("  -text               File in text format (default)");
    err.println("  -xml                File in XML format");
  }  

  /**
   * Parses the command line.
   *
   * @return An error message if the command line was not parsed
   *         sucessfully
   */
  private static String parseCommandLine(String[] args) {
    DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);

    for(int i = 0; i < args.length; i++) {
      if(args[i].equals("-husbandId")) {
	if(++i >= args.length) {
	  return("Missing husband id");
	}

	try {
	  husbandId = Integer.parseInt(args[i]);

	} catch(NumberFormatException ex) {
	  return("Malformatted husband id: " + args[i]);
	}

	if(husbandId < 1) {
	  return("Illegal husband id value: " + husbandId);
	}

      } else if(args[i].equals("-wifeId")) {
	if(++i >= args.length) {
	  return("Missing wife id");
	}

	try {
	  wifeId = Integer.parseInt(args[i]);

	} catch(NumberFormatException ex) {
	  return("Malformatted wife id: " + args[i]);
	}

	if(wifeId < 1) {
	  return("Illegal wife id value: " + husbandId);
	}

      } else if(args[i].equals("-date")) {
	if(++i >= args.length) {
	  return("Missing marriage date");
	}

	// A date will take up three arguments
	StringBuffer sb = new StringBuffer(args[i] + " ");
	for(int j = i+1; j < i+3; j++) {
	  if(j >= args.length) {
	    return("Malformatted date of birth: " + sb);

	  } else {
	    sb.append(args[j] + " ");
	  }
	}
	i = i+2;

	try {
	  date = df.parse(sb.toString().trim());

	} catch(ParseException ex) {
	  return("Malformatted marriage date: " + args[i]);
	}

      } else if(args[i].equals("-location")) {
	if(++i >= args.length) {
	  return("Missing marriage location");
	}

	location = args[i];

      } else if(args[i].equals("-file")) {
	if(++i >= args.length) {
	  return("Missing file name");
	}

	fileName = args[i];

      } else if(args[i].equals("-text")) {
	useXml = false;

      } else if(args[i].equals("-xml")) {
	useXml = true;

      } else {
	return("Unknown command line option: " + args[i]);
      }
    }

    // Make some additional checks
    if(husbandId == -1) {
      return("No husband id specified");

    } else if(wifeId == -1) {
      return("No wife id specified");

    } else if(fileName == null) {
      return("No file specified");
    }

    // No errors
    return(null);
  }

  /**
   * Main program that parses the command line and creates a marriage.
   */
  public static void main(String[] args) {
    // Parse the command line
    String message = parseCommandLine(args);

    if(message != null) {
      err.println("** " + message + "\n");
      usage();
      System.exit(1);
    }

    FamilyTree tree = null;

    // If the data file exists, read it in
    File file = new File(fileName);
    if(file.exists()) {
      Parser parser = null;
      if(useXml) {
	// File in XML format
	try {
	  parser = new XmlParser(file);

	} catch(FileNotFoundException ex) {
	  err.println("** Could not find file " + fileName);
	  System.exit(1);
	}

      } else {
        // File in text format
        try {
          parser = new TextParser(file);

        } catch(FileNotFoundException ex) {
          err.println("** Could not find file " + fileName);
          System.exit(1);
        }

      }

      try {
        tree = parser.parse();

      } catch(ParserException ex) {
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

    if(location != null) {
      marriage.setLocation(location);
    }

    if(date != null) {
      marriage.setDate(date);
    }

    // Now write the family tree to the file
    Dumper dumper = null;
    if(useXml) {
      try {
	dumper = new XmlDumper(file);

      } catch(IOException ex) {
	err.println("** Error while dealing with " + file);
	System.exit(1);
      }

    } else {
      try {
        dumper = new TextDumper(file);

      } catch(IOException ex) {
        err.println("** Error while dealing with " + file);
        System.exit(1);
      }
    }
    dumper.dump(tree);


  }

}
