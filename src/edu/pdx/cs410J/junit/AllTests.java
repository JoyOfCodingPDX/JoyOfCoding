package edu.pdx.cs410J.junit;

import junit.framework.*;

/**
 * This program runs all of the unit tests in the junit package
 */
public class AllTests {

  public static void main(String[] args) {
    TestSuite suite = new TestSuite();
    suite.addTest(new TestSuite(SectionTest.class));
    junit.textui.TestRunner.run(suite);
  }

}
