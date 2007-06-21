package edu.pdx.cs399J.junit;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import junit.framework.*;

/**
 * This program Java relfection to run some number of test methods in
 * a given test case class.
 */
public class TestRunner {
  private static final PrintStream err = System.err;

  /**
   * Prints usage information for this program
   */
  private static void usage(String s) {
    err.println("\n** " + s + "\n");
    err.println("usage: java TestRunner className (methodName)*");
    err.println("  className    The name of the TestCase class");
    err.println("  methodName   The name of a test method to run");
    err.println("");
    err.println("This program runs some number of test methods in " +
                "a given test class.  If no method is provided, " +
                "then all of the methods are run");
    err.println("");
    System.exit(1);
  }

  /**
   * The main program
   */
  @SuppressWarnings("unchecked")
  public static void main(String[] args) throws Throwable {
    String className = null;
    List<String> methodNames = new ArrayList<String>();

    for (int i = 0; i < args.length; i++) {
      if (className == null) {
        className = args[i];

      } else {
        methodNames.add(args[i]);
      }
    }

    if (className == null) {
      usage("Missing class name");
    }

    Class<TestCase> c = (Class<TestCase>) Class.forName(className);
    TestSuite suite;

    if (methodNames.isEmpty()) {
      suite = new TestSuite(c);

    } else {
      suite = new TestSuite();

      Constructor init =
        c.getConstructor(new Class[] { String.class });
      Iterator iter = methodNames.iterator();
      while (iter.hasNext()) {
        String methodName = (String) iter.next();
        TestCase tc =
          (TestCase) init.newInstance(new Object[] { methodName });
        suite.addTest(tc);
      }
    }

    junit.textui.TestRunner.run(suite);
  }

}
