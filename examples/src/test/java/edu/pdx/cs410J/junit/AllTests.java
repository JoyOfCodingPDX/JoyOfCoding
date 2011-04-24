package edu.pdx.cs410J.junit;

import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This program runs all of the unit tests in the junit package
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({SectionTest.class, CourseTest.class})
public class AllTests {

  public static void main(String[] args) {
      JUnitCore.runClasses( AllTests.class );
  }
}
