package edu.pdx.cs410J.grader;

import java.io.*;
import java.util.*;

/**
 * This class is used to grade Project 1.
 *
 * @author David Whitlock
 */
public class TestProject1 {

  private static PrintWriter out = new PrintWriter(System.out, true);
  private static PrintWriter err = new PrintWriter(System.err, true);

  private static final String PACKAGE = "edu.pdx.cs410J.";
  private static final String PROJECT = ".Project1";

  private static String[] testDescriptions = {
    "Test 1: No arguments -- how is your help?",
    "Test 2: Unknown command line argument",
    "Test 3: Creating a new appointment book",
    "Test 4: Adding another appointment",
    "Test 5: Multi-string message",
    "Test 6: Bad text file format",
    "Test 7: Bad date format",
    "Test 8: Bad time format",
    "Test 9: No message for appointment",
    "Test 10: Not enough arguments",
    "Test 11: Do you support \"owner\"?"
  };

  private static String[][] testArgs = {
    {},
    {"-beginTime", "15:45", "-badParam", "aString"},
    {"-beginDay", "10/14/2000", "-beginTime", "10:00", "-endDay",
     "10/14/2000", "-endTime", "18:00", "-message", 
     "Test3", "-textFile", "{STUDENT}.txt"},
    {"-beginDay", "10/15/2000", "-beginTime", "10:00", "-endDay",
     "10/15/2000", "-endTime", "18:00", "-message", 
     "Test4", "-textFile", "{STUDENT}.txt"},
    {"-beginDay", "04/24/2000", "-endDay", "04/24/2000", "-beginTime",
     "08:00", "-endTime", "10:00", "-message", "Test5: ", "Meet", "with",
     "boss!", "-textFile", "{STUDENT}.txt"},
    {"-beginDay", "01/01/2001", "-endDay", "01/02/2001", "-beginTime",
     "00:00", "-endTime", "23:59", "-message", "Test6",
     "-textFile", "bogus.txt"},
    {"-beginDay", "05/17/2000", "-endDay", "03/FRED/2000", "-beginTime",
     "08:00", "-endTime", "10:00", "-message", "Test7", "-textFile", 
     "{STUDENT}.txt"},
    {"-beginDay", "10/19/2000", "-beginTime", "10:LUIS", "-endDay",
     "10/21/2004", "-endTime", "18:00", "-message", 
     "Test8", "-textFile", "{STUDENT}.txt"},
    {"-beginDay", "11/15/2000", "-beginTime", "11:00", "-endDay",
     "10/11/2011", "-endTime", "11:11", "-textFile", "{STUDENT}.txt",
    "-message"},
    {"-beginDay", "10/14/2000", "-beginTime", "10:00", "-endDay",
     "10/14/2000", "-message", "Test10", "-textFile", "{STUDENT}.txt"},
    {"-beginDay", "12/12/2000", "-beginTime", "12:00", "-endDay",
     "12/12/2000", "-endTime", "18:00", "-owner", "Test", "-message", 
     "Test11", "-textFile", "{STUDENT}.txt"}
  };

  /**
   * Print out usage information for this program.
   */
  private static void usage() {
    err.println("usage: java TestProject1 [options] gradeBookFile");
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
    String gradeBookFile = null;

    // Parse the command line
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-logDir")) {
	if(++i >= args.length) {
	  err.println("** Missing log dir");
	  usage();
	}

	logDirName = args[i];

      } else if (args[i].startsWith("-")) {
	err.println("** Unknown option: " + args[i]);
	usage();

      } else if (gradeBookFile == null) {
	gradeBookFile = args[i];
      }
    }

    if (gradeBookFile == null) {
      err.println("** Missing grade book file");
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

    // Install a TesterSecurityManager that will allow us to run a
    // main method multiple times without exiting.
    TesterSecurityManager tsm = new TesterSecurityManager();
    System.setSecurityManager(tsm);

    // Get the id of every student in the class and run his/her
    // project through the tester
    Iterator ids = (new HashSet()).iterator();
    while (ids.hasNext()) {
      String id = (String) ids.next();

      String className = PACKAGE + id + PROJECT;
      
      Tester tester = new Tester(id, logDir);
      tester.printBanner("CS410J Project 1: " + className, '-');
      tester.println("");
      
      // Test 1
      tester.printBanner(testDescriptions[0], '*');
      tester.executeMain(className, testArgs[0]);
      tester.println("");
      
      // Test 2
      tester.printBanner(testDescriptions[1], '*');
      tester.executeMain(className, testArgs[1]);
      tester.println("");

      // Test 3
      tester.printBanner(testDescriptions[2], '*');
      tester.executeMain(className, testArgs[2]);
      tester.cat(new File(id + ".txt"));
      tester.println("");

      // Test 4
      tester.printBanner(testDescriptions[3], '*');
      tester.executeMain(className, testArgs[3]);
      tester.cat(new File(id + ".txt"));
      tester.println("");

      // Test 5
      tester.printBanner(testDescriptions[4], '*');
      tester.executeMain(className, testArgs[4]);
      tester.cat(new File(id + ".txt"));
      tester.println("");

      // Test 6
      tester.printBanner(testDescriptions[5], '*');
      tester.cp(new File("bogus.orig"), new File("bogus.txt"));
      tester.cat(new File("bogus.txt"));
      tester.executeMain(className, testArgs[5]);
      tester.cat(new File("bogus.txt"));
      tester.println("");

      // Test 7
      tester.printBanner(testDescriptions[6], '*');
      tester.executeMain(className, testArgs[6]);
      tester.cat(new File(id + ".txt"));
      tester.println("");

      // Test 8
      tester.printBanner(testDescriptions[7], '*');
      tester.executeMain(className, testArgs[7]);
      tester.cat(new File(id + ".txt"));
      tester.println("");

      // Test 9
      tester.printBanner(testDescriptions[8], '*');
      tester.executeMain(className, testArgs[8]);
      tester.cat(new File(id + ".txt"));
      tester.println("");

      // Test 10
      tester.printBanner(testDescriptions[9], '*');
      tester.executeMain(className, testArgs[9]);
      tester.cat(new File(id + ".txt"));
      tester.println("");

      // Test 11
      tester.printBanner(testDescriptions[10], '*');
      tester.executeMain(className, testArgs[10]);
      tester.cat(new File(id + ".txt"));
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
