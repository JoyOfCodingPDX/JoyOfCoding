package edu.pdx.cs399J.grader;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This class is used to grade Project 4.
 *
 * @author David Whitlock
 */
public class TestProject4 {

  private static PrintWriter out = new PrintWriter(System.out, true);
  private static PrintWriter err = new PrintWriter(System.err, true);

  private static final String PACKAGE = "edu.pdx.cs399J.";
  private static final String CLIENT = ".Project4";
  private static final String SERVER = ".AppointmentBookServer";

  private static String[] testDescriptions = {
    "Test 1: Help for your server",
    "Test 2: Starting your server on an illegal port",
    "Test 3: Starting your server with new appointment book",
    "Test 4: Help for your client",
    "Test 5: Starting client on port with no server",
    "Test 6: Querying an empty appointment book",
    "Test 7: Adding an appointment",
    "Test 8: Querying an appointment book",
    "Test 9: Adding another appointment",
    "Test 10: Adding a third appointment",
    "Test 11: Querying an appointment book",
    "Test 12: Shutting down the server",
    "Test 13: Restarting server",
    "Test 14: Adding a fourth appointment",
    "Test 15: Final query",
    "Test 16: Final shutdown"
  };

  private static String[][] testArgs = {
    {},
    {"-port",  "42", "-xmlFile", "{STUDENT}.xml"},
    {"-port", "12345", "-xmlFile", "{STUDENT}.xml"},
    {},
    {"-host", "localhost", "-port", "12346", "-begin", "11/19/00",
     "3:00", "PM", "-end", "11/19/00", "4:00", "PM", "-message",
     "Test5"},
    {"-host", "localhost", "-port", "12345", "-begin", "11/19/00",
     "3:00", "PM", "-end", "11/19/00", "4:00", "PM"},
    {"-host", "localhost", "-port", "12345", "-begin", "11/19/00",
     "1:00", "PM", "-end", "11/19/00", "2:00", "PM", "-message", 
     "Test7"},
    {"-host", "localhost", "-port", "12345", "-begin", "11/19/00",
     "12:30", "PM", "-end", "11/19/00", "3:00", "PM"},
    {"-host", "localhost", "-port", "12345", "-begin", "11/20/00",
     "3:00", "PM", "-end", "11/20/00", "4:00", "PM", "-message", 
     "Test9"},
    {"-host", "localhost", "-port", "12345", "-begin", "11/18/00",
     "10:00", "AM", "-end", "11/18/00", "11:00", "AM", "-message", 
     "Test10"},
    {"-host", "localhost", "-port", "12345", "-begin", "11/18/00",
     "9:30", "AM", "-end", "11/19/00", "4:00", "PM"},
    {"-host", "localhost", "-port", "12345", "-begin", "11/18/99",
     "9:30", "AM", "-end", "11/19/99", "4:00", "PM", "-shutdown"},
    {"-port", "12345", "-xmlFile", "{STUDENT}.xml"}, 
    {"-host", "localhost", "-port", "12345", "-begin", "11/20/00",
     "10:00", "AM", "-end", "11/20/00", "11:00", "AM", "-message", 
     "Test14"},
    {"-host", "localhost", "-port", "12345", "-begin", "11/20/00",
     "12:00", "AM", "-end", "11/21/00", "12:00", "AM"},
    {"-host", "localhost", "-port", "12345", "-begin", "11/18/99",
     "9:30", "AM", "-end", "11/19/99", "4:00", "PM", "-shutdown"}

  };

  /**
   * Print out usage information for this program.
   */
  private static void usage() {
    err.println("usage: java TestProject3 [options] (student)*");
    err.println("  Where [options] are:");
    err.println("  -logDir dir      Directory to place grade logs");
    err.println("  -studentClasses path   Where to look for " +
		"students' classes");
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
	if(++i >= args.length) {
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

      String clientName = PACKAGE + id + CLIENT;
      String serverName = PACKAGE + id + SERVER;
      
      Tester tester = new Tester(id, logDir);
      tester.setURLs(urls);

      tester.printBanner("CS399J Project 4: " + clientName, '-');
      tester.println("");
      
      Thread server;

//        tester.cp(new File("apptbook.xml"), new File(id + ".xml"));
//        tester.cat(new File(id + ".xml"));

      // Test 1
      tester.printBanner(testDescriptions[0], '*');
      server = tester.executeMainInThread(serverName, testArgs[0]);
      try {
	// Wait for it...
	server.join();
      } catch (InterruptedException ex) {
	ex.printStackTrace();
	return;
      }
      tester.println("");
      
      // Test 2
      tester.printBanner(testDescriptions[1], '*');
      server = tester.executeMainInThread(serverName, testArgs[1]);
      try {
	// Wait for it...
	server.join();
      } catch (InterruptedException ex) {
	ex.printStackTrace();
	return;
      }
      tester.println("");

      // Test 3
      tester.printBanner(testDescriptions[2], '*');
      tester.executeMainInThread(serverName, testArgs[2]);
      if (!tester.wait(5)) {
	return;
      }
      tester.println("");

      // Test 4
      tester.printBanner(testDescriptions[3], '*');
      tester.executeMain(clientName, testArgs[3]);
      tester.println("");

      // Test 5
      tester.printBanner(testDescriptions[4], '*');
      tester.executeMain(clientName, testArgs[4]);
      tester.println("");

      // Test 6
      tester.printBanner(testDescriptions[5], '*');
      tester.executeMain(clientName, testArgs[5]);
      tester.println("");

      // Test 7
      tester.printBanner(testDescriptions[6], '*');
      tester.executeMain(clientName, testArgs[6]);
      tester.println("");

      // Test 8
      tester.printBanner(testDescriptions[7], '*');
      tester.executeMain(clientName, testArgs[7]);
      tester.println("");

      // Test 9
      tester.printBanner(testDescriptions[8], '*');
      tester.executeMain(clientName, testArgs[8]);
      tester.println("");

      // Test 10
      tester.printBanner(testDescriptions[9], '*');
      tester.executeMain(clientName, testArgs[9]);
      tester.println("");

      // Test 11
      tester.printBanner(testDescriptions[10], '*');
      tester.executeMain(clientName, testArgs[10]);
      tester.println("");

      // Test 12
      tester.printBanner(testDescriptions[11], '*');
      tester.executeMain(clientName, testArgs[11]);
      try {
	// Wait for server to shut down
	server.join();
      } catch (InterruptedException ex) {
	ex.printStackTrace();
	return;
      }
      tester.println("");

      // Test 13
      tester.printBanner(testDescriptions[12], '*');
      server = tester.executeMainInThread(serverName, testArgs[12]);
      if (!tester.wait(5)) {
	return;
      }
      tester.println("");

      // Test 14
      tester.printBanner(testDescriptions[13], '*');
      tester.executeMain(clientName, testArgs[13]);
      tester.println("");

      // Test 15
      tester.printBanner(testDescriptions[14], '*');
      tester.executeMain(clientName, testArgs[14]);
      tester.println("");

      // Test 16
      tester.printBanner(testDescriptions[15], '*');
      tester.executeMain(clientName, testArgs[15]);
      tester.println("");

//        // Shutdown
//        tester.executeMain(clientName, 
//  			 new String[] {"-host", "localhost", "-port",
//  				       "12345", "-begin", "11/12/00",
//  				       "2:00", "PM", "-end",
//  				       "11/12/00", "3:00", "PM",
//  				       "-message", "bye",
//  				       "-shutdown"});

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
