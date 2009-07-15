package edu.pdx.cs399J.junit;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * This class tests the functionality of the <code>Section</code> class
 */
public class SectionTest {

  @Test
  public void testAddStudent() {
    Student student = new Student("123-45-6789");
    Course course = new Course("CS", 410, 4);
    Section section = 
      new Section(course, Section.SPRING, 2001);
    section.addStudent(student);
    assertEquals(1, section.getClassSize());
  }

  @Test
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
