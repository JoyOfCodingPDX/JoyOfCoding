package edu.pdx.cs410J.junit;

import org.junit.Test;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

/**
 * This class tests the functionality of the <code>Course</code> class.
 */
public class CourseTest {

  @Test
  public void testGoodCourse() {
    new Course("Computer Science", 410, 4);
  }

  @Test
  public void testCourseNumberLessThan100() {
    try {
      new Course("Computer Science", 17, 4);
      fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  @Test
  public void testCreditLessThanZero() {
    try {
      new Course("Computer Science", 410, -3);
      fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  @Test
  public void testGetCredits() {
    int credits = 4;
    Course c = new Course("Computer Science", 410, credits);
    assertEquals(credits, c.getCredits());
  }
  
}
