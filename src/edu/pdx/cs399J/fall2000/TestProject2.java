package edu.pdx.cs399J.grader;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This class is used to grade Project 2.
 *
 * @author David Whitlock
 */
public class TestProject2 {

  private static PrintWriter out = new PrintWriter(System.out, true);
  private static PrintWriter err = new PrintWriter(System.err, true);

  private static final String PACKAGE = "edu.pdx.cs399J.";
  private static final String PROJECT = ".Project2";

  private static String[] testDescriptions = {
    "Test 1: No arguments -- how is your help?",
    "Test 2: Adding an appointment",
    "Test 3: Adding another appointment",
    "Test 4: Adding a 3rd appointment -- Are they sorted?",
    "Test 5: Bad date format",
    "Test 6: Bad time format",
    "Test 7: Adding a 4th appointment -- Duplicate begin time",
    "Test 8: Adding a 5th appointment -- Duplicate begin and end times",
    "Test 9: How pretty are you?",
    "Test 10: Bad text file format",
    "Test 11: Multi-string message",
    "Test 12: Owner"
  };

  private static String[][] testArgs = {
    {},
    {"-begin", "10/26/00", "1:30", "pm", "-end", "10/26/00", "2:00",
     "pm", "-message", "Test2", "-textFile", "{STUDENT}.txt"},
    {"-begin", "10/28/00", "1:30", "pm", "-end", "10/28/00", "2:00",
     "pm", "-message", "Test3", "-textFile", "{STUDENT}.txt"},
    {"-begin", "10/27/00", "1:30", "pm", "-end", "10/27/00", "2:00",
     "pm", "-message", "Test4", "-textFile", "{STUDENT}.txt"},
    {"-begin", "10/30", "12:26", "am", "-end", "10/30/00", "1:00",
     "am", "-message", "Test5", "-textFile", "{STUDENT}.txt"},
    {"-begin", "10/27/00", "1:aa", "pm", "-end", "10/27/00", "2:00",
     "pm", "-message", "Test6", "-textFile", "{STUDENT}.txt"},
    {"-begin", "10/27/00", "1:30", "pm", "-end", "10/27/00", "2:30",
     "pm", "-message", "Test7", "-textFile", "{STUDENT}.txt"},
    {"-begin", "10/27/00", "1:30", "pm", "-end", "10/27/00", "2:00",
     "pm", "-message", "Test8", "-textFile", "{STUDENT}.txt"},
    {"-begin", "10/15/00", "12:00", "pm", "-end", "10/15/00", "1:00",
     "pm", "-message", "Test9", "-textFile", "{STUDENT}.txt",
     "-prettyPrint", "{STUDENT}.pretty"},
    {"-begin", "10/27/00", "1:30", "pm", "-end", "10/27/00", "2:00",
     "pm", "-message", "Test10", "-textFile", "bogus.txt"},
    {"-begin", "10/31/00", "7:00", "pm", "-end", "10/31/00", "11:00",
     "pm", "-message", "Happy", "Halloween", "(Test11)", "-textFile",
     "{STUDENT}.txt"}, 
    {"-begin", "10/14/00", "1:00", "pm", "-end", "10/17/00", "1:00",
     "pm", "-owner", "Tester", "-message", "Test12", "-textFile",
     "{STUDENT}.txt"}
  };

  /**
   * Print out usage information for this program.
   */
  private static void usage() {
    err.println("usage: java TestProject2 [options] (student)*");
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
      
      Tester tester = new Tester(id, logDir);
      tester.setURLs(urls);

      tester.printBanner("CS399J Project 2: " + className, '-');
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
      tester.executeMain(className, testArgs[5]);
      tester.cat(new File(id + ".txt"));
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
      tester.cat(new File(id + ".pretty"));
      tester.println("");

      // Test 10
      tester.printBanner(testDescriptions[9], '*');
      tester.cp(new File("bogus.orig"), new File("bogus.txt"));
      tester.cat(new File("bogus.txt"));
      tester.executeMain(className, testArgs[9]);
      tester.cat(new File(id + ".txt"));
      tester.println("");

      // Test 11
      tester.printBanner(testDescriptions[10], '*');
      tester.executeMain(className, testArgs[10]);
      tester.cat(new File(id + ".txt"));
      tester.println("");

      // Test 12
      tester.printBanner(testDescriptions[11], '*');
      tester.executeMain(className, testArgs[11]);
      tester.cat(new File(id + ".txt"));
      tester.println("");

      tester.done();
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

      tester.executeMain("edu.pdx.cs399J.grader.TestProject1",
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
