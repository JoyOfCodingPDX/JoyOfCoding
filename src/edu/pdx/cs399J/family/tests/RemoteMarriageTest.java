package edu.pdx.cs399J.family.tests;

import edu.pdx.cs399J.family.*;

import java.rmi.*;
import junit.framework.*;

/**
 * This class tests the functionality of the implementors of
 * <code>RemoteMarriage</code>.
 */
public class RemoteMarriageTest extends RemoteTestCase {

  public RemoteMarriageTest(String name) {
    super(name);
  }

  public static Test suite() {
    return(new TestSuite(RemoteMarriageTest.class));
  }

  /**
   * Run one test method from this class
   */
  public static void main(String[] args) throws Throwable {
    TestSuite suite = new TestSuite();

    if (args.length == 0) {
      suite.addTest(suite());

    } else {
      for (int i = 0; i < args.length; i++) {
        suite.addTest(new RemoteMarriageTest(args[i]));
      }
    }

    junit.textui.TestRunner.run(suite);
  }

  ////////  Test Methods

  public void testSetDate() {
    
  }

  public void testSetLocation() {

  }

}
