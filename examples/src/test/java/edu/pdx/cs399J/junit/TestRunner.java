package edu.pdx.cs399J.junit;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

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

      for ( String arg : args ) {
          if ( className == null ) {
              className = arg;

          } else {
              methodNames.add( arg );
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

      Constructor<TestCase> init =
        c.getConstructor(new Class[] { String.class });
        for ( String methodName : methodNames )
        {
            TestCase tc = init.newInstance( methodName );
            suite.addTest( tc );
        }
    }

    junit.textui.TestRunner.run(suite);
  }

}
