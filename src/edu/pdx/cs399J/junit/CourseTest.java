package edu.pdx.cs410J.junit;

import junit.framework.*;

/**
 * This class tests the functionality of the <code>Course</code> class.
 */
public class CourseTest extends TestCase {

  /**
   * Returns a suite containing all of the tests in this class
   */
  public static Test suite() {
    return(new TestSuite(CourseTest.class));
  }

  /**
   * Creates a new <code>CourseTest</code> for running the test of a
   * given name
   */
  public CourseTest(String name) {
    super(name);
  }

  //////// main program

  /**
   * A program that allow the user to run tests as named on the
   * command line.
   */
  public static void main(String[] args) {
    TestSuite suite = new TestSuite();

    if (args.length == 0) {
      suite.addTest(suite());

    } else {
      for (int i = 0; i < args.length; i++) {
        suite.addTest(new CourseTest(args[i]));
      }
    }

    junit.textui.TestRunner.run(suite);
  }

  ////////  Test cases

  public void testGoodCourse() {
    Course c = new Course("Computer Science", 410, 4);
  }

  public void testCourseNumberLessThan100() {
    try {
      Course c = new Course("Computer Science", 17, 4);
      fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  public void testCreditLessThanZero() {
    try {
      Course c = new Course("Computer Science", 410, -3);
      fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  public void testGetCredits() {
    int credits = 4;
    Course c = new Course("Computer Science", 410, credits);
    assertEquals(credits, c.getCredits());
  }
  
}
