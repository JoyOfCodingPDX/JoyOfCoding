package edu.pdx.cs410J.grader;

import java.io.*;
import java.util.*;

import java.net.*;

/**
 * This class is used to grade Project 2.
 *
 * @author David Whitlock
 */
public class TestProject3 {

  private static PrintWriter out = new PrintWriter(System.out, true);
  private static PrintWriter err = new PrintWriter(System.err, true);

  private static final String PACKAGE = "edu.pdx.cs410J.";
  private static final String PROJECT = ".Project3";

  private static String[] testDescriptions = {
    "Test 1: No arguments -- how is your help?",
    "Test 2: Adding an appointment to a text file",
    "Test 3: Adding an appointment to an XML file",
    "Test 4: Adding another appointment to an XML file",
    "Test 5: Converting a text file to an XML file",
    "Test 6: Adding an appointment to the converted file",
    "Test 7: Handling a completely bogus file",
    "Test 8: Handling a non-conforming XML file",
    "Test 9: Handling someone else's XML file"
  };

  private static String[][] testArgs = {
    {},
    {"-begin", "10/26/00", "1:30", "pm", "-end", "10/26/00", "2:00",
     "pm", "-message", "Test2", "-textFile", "{STUDENT}.txt"},
    {"-begin", "10/28/00", "1:30", "pm", "-end", "10/28/00", "2:00",
     "pm", "-message", "Test3", "-xmlFile", "{STUDENT}.xml"},
    {"-begin", "10/27/00", "1:30", "pm", "-end", "10/27/00", "2:00",
     "pm", "-message", "Test4", "-xmlFile", "{STUDENT}.xml"},
    {"{STUDENT}.txt", "{STUDENT}-converted.xml"},
    {"-begin", "10/30/00", "12:26", "am", "-end", "10/30/00", "1:00",
     "am", "-message", "Test6", "-xmlFile", "{STUDENT}-converted.xml"},
    {"-begin", "10/27/00", "1:00", "pm", "-end", "10/27/00", "2:00",
     "pm", "-message", "Test7", "-xmlFile", "completely-bogus.xml"},
    {"-begin", "10/27/00", "1:30", "pm", "-end", "10/27/00", "2:30",
     "pm", "-message", "Test8", "-xmlFile", "bogus.xml"},
    {"-begin", "10/27/00", "1:30", "pm", "-end", "10/27/00", "2:00",
     "pm", "-message", "Test9", "-xmlFile", "other.xml"}
  };

  /**
   * Print out usage information for this program.
   */
  private static void usage() {
    err.println("usage: java TestProject3 [options] (student)*");
    err.println("  Where [options] are:");
    err.println("  -logDir dir      Directory to place grade logs");
    System.exit(1);
  }

  /**
   * Program that grades Project 1 for the students contained a given
   * grade book.  
   */
  public static void main(String[] args) {
    String logDirName = null;
    Set students = new HashSet();
    String studentClassPath = "";

    // Parse the command line
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-logDir")) {
	if(++i >= args.length) {
	  err.println("** Missing log dir");
	  usage();
	}

	logDirName = args[i];

      } else if (args[i].equals("-studentClasses")) {
        if (++i >= args.length) {
          err.println("** Missing student class path");
          usage();
        }

        studentClassPath = args[i];

      } else if (args[i].startsWith("-")) {
	err.println("** Unknown option: " + args[i]);
	usage();

      } else {
	students.add(args[i]);
      }
    }

    if (students.isEmpty()) {
      err.println("** No students specified");
      usage();
    }

    File logDir;
    if (logDirName == null) {
      String cwd = System.getProperty("user.dir");
      logDir = new File(cwd);

    } else {
      logDir = new File(logDirName);
    }

    if (!logDir.exists()) {
      logDir.mkdirs();
    }

    URL[] urls = Tester.parseURLPath(studentClassPath);

    // Install a TesterSecurityManager that will allow us to run a
    // main method multiple times without exiting.
    TesterSecurityManager tsm = new TesterSecurityManager();
    System.setSecurityManager(tsm);

    // Get the id of every student in the class and run his/her
    // project through the tester
    Iterator ids = students.iterator();
    while (ids.hasNext()) {
      String id = (String) ids.next();

      String className = PACKAGE + id + PROJECT;
      String converterName = PACKAGE + id + ".Converter";
      
      Tester tester = new Tester(id, logDir);
      tester.setURLs(urls);

      tester.printBanner("CS410J Project 3: " + className, '-');
      tester.println("");
      
      // Test 1
      tester.printBanner(testDescriptions[0], '*');
      tester.executeMain(className, testArgs[0]);
      tester.println("");
      
      // Test 2
      tester.printBanner(testDescriptions[1], '*');
      tester.executeMain(className, testArgs[1]);
      tester.cat(new File(id + ".txt"));
      tester.println("");

      // Test 3
      tester.printBanner(testDescriptions[2], '*');
      tester.executeMain(className, testArgs[2]);
      tester.cat(new File(id + ".xml"));
      tester.println("");

      // Test 4
      tester.printBanner(testDescriptions[3], '*');
      tester.executeMain(className, testArgs[3]);
      tester.cat(new File(id + ".xml"));
      tester.println("");

      // Test 5
      tester.printBanner(testDescriptions[4], '*');
      tester.printBanner(converterName, ' ');
      tester.cat(new File(id + ".txt"));
      tester.executeMain(converterName, testArgs[4]);
      tester.cat(new File(id + "-converted.xml"));
      tester.println("");

      // Test 6
      tester.printBanner(testDescriptions[5], '*');
      tester.executeMain(className, testArgs[5]);
      tester.cat(new File(id + "-converted.xml"));
      tester.println("");

      // Test 7
      tester.printBanner(testDescriptions[6], '*');
      tester.cp(new File("completely-bogus.orig"), 
		new File("completely-bogus.xml"));
      tester.cat(new File("completely-bogus.xml"));
      tester.executeMain(className, testArgs[6]);
      tester.cat(new File("completely-bogus.xml"));
      tester.println("");

      // Test 8
      tester.printBanner(testDescriptions[7], '*');
      tester.cp(new File("bogus.orig"), new File("bogus.xml"));
      tester.cat(new File("bogus.xml"));
      tester.executeMain(className, testArgs[7]);
      tester.cat(new File("bogus.xml"));
      tester.println("");

      // Test 9
      tester.printBanner(testDescriptions[8], '*');
      tester.cp(new File("other.orig"), new File("other.xml"));
      tester.cat(new File("other.xml"));
      tester.executeMain(className, testArgs[8]);
      tester.cat(new File("other.xml"));
      tester.println("");

    }

    // Allow us to exit
    tsm.setAllowExit(true);
  }

  /**
   * Old Test program.
   */
  public static void main0(String[] args) {
    // If there are arguments, print them.  Otherwise, call
    // executeMain to do the work
    if (args.length == 0) {
      // Make a new Tester
      String userDir = System.getProperty("user.dir");
      File cwd = new File(userDir);
      Tester tester = new Tester("testProject1", cwd);

      tester.executeMain("edu.pdx.cs410J.grader.TestProject1",
			 testArgs[2]);

    } else {
      for (int i = 0; i < args.length; i++) {
	System.out.print(args[i]);
	if(i < args.length - 1) {
	  System.out.print(", ");
	} else {
	  System.out.println("");
	}
      }
    }
  }
}
