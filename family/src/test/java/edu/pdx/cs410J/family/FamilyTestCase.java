package edu.pdx.cs410J.family;

import org.junit.jupiter.api.Assertions;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the abstract superclass of classes that test family tree
 * classes.  It contains a number of helper methods.
 */
public abstract class FamilyTestCase {


  /**
   * Asserts that two <code>Person</code>s have the same contents
   */
  void assertEquals(Person p1, Person p2) {
    if (p1 == null) {
      assertNull(p2);
      return;

    } else if (p2 == null) {
      assertNull(p1);
      return;
    }

    assertNotNull(p1);
    assertNotNull(p2);

    Assertions.assertEquals(p1.getId(), p2.getId());
    Assertions.assertEquals(p1.getGender(), p2.getGender());
    Assertions.assertEquals(p1.getFirstName(), p2.getFirstName());
    Assertions.assertEquals(p1.getMiddleName(), p2.getMiddleName());
    Assertions.assertEquals(p1.getLastName(), p2.getLastName());
    assertEquals(p1.getMother(), p2.getMother());
    assertEquals(p1.getFather(), p2.getFather());
    assertEquals(p1.getDateOfBirth(), p2.getDateOfBirth());
    assertEquals(p1.getDateOfDeath(), p2.getDateOfDeath());

    Assertions.assertEquals(p1.getMarriages().size(), p2.getMarriages().size());

    Iterator<Marriage> iter1 = p1.getMarriages().iterator();
    Iterator<Marriage> iter2 = p2.getMarriages().iterator();
    while (iter1.hasNext() && iter2.hasNext()) {
      assertEquals( iter1.next(), iter2.next() );
    }

  }

  /**
   * Asserts that two <code>Date</code> represent the same day
   * (ignores times)
   */
  void assertEquals(Date d1, Date d2) {
    if (d1 == null) {
      assertNull(d2);
      return;

    } else if (d2 == null) {
      assertNull(d1);
      return;
    }

    Calendar c1 = Calendar.getInstance();
    c1.setTime(d1);
    Calendar c2 = Calendar.getInstance();
    c2.setTime(d2);

    Assertions.assertEquals(c1.get(Calendar.DAY_OF_YEAR),
                 c2.get(Calendar.DAY_OF_YEAR));
    Assertions.assertEquals(c1.get(Calendar.YEAR), c2.get(Calendar.YEAR));
  }

  /**
   * Asserts that two <code>Marriage</code>s are the same
   */
  void assertEquals(Marriage m1, Marriage m2) {
    if (m1 == null) {
      assertNotNull(m2);
      return;

    } else if (m2 == null) {
      assertNull(m1);
      return;
    }

    // To avoid infinite recursion, we compare people using their ids
    Assertions.assertEquals(m1.getHusband().getId(), m2.getHusband().getId());
    Assertions.assertEquals(m1.getWife().getId(), m2.getWife().getId());
    assertEquals(m1.getDate(), m2.getDate());
    Assertions.assertEquals(m1.getLocation(), m2.getLocation());
  }

  /**
   * Asserts that two <code>FamilyTree</code>s has the same contents
   */
  void assertEquals(FamilyTree tree1, FamilyTree tree2) {
    Assertions.assertEquals(tree1.getPeople().size(), tree2.getPeople().size());

      for ( Person person : tree1.getPeople() )
      {
          Person p2 = tree2.getPerson( person.getId() );
          assertNotNull( p2 );
          assertEquals( person, p2 );
      }
  }

  /**
   * Asserts that one String contains another
   */
  void assertContains(String container, String containee) {
    assertTrue(container.indexOf(containee) != -1);
  }

  /**
   * Asserts that one String contains another
   */
  void assertContains(String message, String container,
                      String containee) {
    assertTrue(container.indexOf(containee) != -1, message);
  }

}
