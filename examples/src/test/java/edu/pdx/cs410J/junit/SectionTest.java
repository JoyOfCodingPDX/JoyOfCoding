package edu.pdx.cs410J.junit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This class tests the functionality of the <code>Section</code> class
 */
class SectionTest {

  @Test
  void testAddStudent() {
    Student student = new Student("123-45-6789");
    Course course = new Course("CS", 410, 4);
    Section section = 
      new Section(course, Section.SUMMER, 2021);
    section.addStudent(student);
    assertEquals(1, section.getClassSize());
  }

  @Test
  void testDropStudentNotEnrolled() {
    Student student = new Student("123-45-6789");
    Course course = new Course("CS", 410, 4);
    Section section = 
      new Section(course, Section.SUMMER, 2021);
    assertThrows(IllegalArgumentException.class, () -> section.dropStudent(student));
  }

}
