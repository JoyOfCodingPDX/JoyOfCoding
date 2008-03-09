package edu.pdx.cs399J.junit;

import junit.framework.*;

/**
 * This class tests the functionality of the <code>Section</code> class
 */
public class SectionTest extends TestCase {

  /**
   * Returns a suite containing all of the tests in this class
   */
  public static Test suite() {
    return(new TestSuite(SectionTest.class));
  }

  /**
   * Creates a new <code>SectionTest</code> for running the test of a
   * given name
   */
  public SectionTest(String name) {
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
        suite.addTest(new SectionTest(args[i]));
      }
    }

    junit.textui.TestRunner.run(suite);
  }

  ////////  Test Cases

  public void testAddStudent() {
    Student student = new Student("123-45-6789");
    Course course = new Course("CS", 410, 4);
    Section section = 
      new Section(course, Section.SPRING, 2001);
    section.addStudent(student);
    assertEquals(1, section.getClassSize());
  }

  public void testDropStudentNotEnrolled() {
    Student student = new Student("123-45-6789");
    Course course = new Course("CS", 410, 4);
    Section section = 
      new Section(course, Section.SPRING, 2001);
    try {
      section.dropStudent(student);
      fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

}
