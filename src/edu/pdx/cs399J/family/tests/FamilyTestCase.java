package edu.pdx.cs399J.family.tests;

import edu.pdx.cs399J.family.*;
import java.util.*;
import junit.framework.*;

/**
 * This is the abstract superclass of classes that test family tree
 * classes.  It contains a number of helper methods.
 */
public abstract class FamilyTestCase extends TestCase {

  public FamilyTestCase(String name) {
    super(name);
  }

  ////////  Helper methods

  /**
   * Asserts that two <code>Person</code>s have the same contents
   */
  void assertEquals(Person p1, Person p2) {
    if (p1 == null) {
      assertTrue(p2 == null);
      return;

    } else if (p2 == null) {
      assertTrue(p1 == null);
      return;
    }

    assertNotNull(p1);
    assertNotNull(p2);

    assertEquals(p1.getId(), p2.getId());
    assertEquals(p1.getGender(), p2.getGender());
    assertEquals(p1.getFirstName(), p2.getFirstName());
    assertEquals(p1.getMiddleName(), p2.getMiddleName());
    assertEquals(p1.getLastName(), p2.getLastName());
    assertEquals(p1.getMother(), p2.getMother());
    assertEquals(p1.getFather(), p2.getFather());
    assertEquals(p1.getDateOfBirth(), p2.getDateOfBirth());
    assertEquals(p1.getDateOfDeath(), p2.getDateOfDeath());

    assertEquals(p1.getMarriages().size(), p2.getMarriages().size());

    Iterator iter1 = p1.getMarriages().iterator();
    Iterator iter2 = p2.getMarriages().iterator();
    while (iter1.hasNext() && iter2.hasNext()) {
      Marriage m1 = (Marriage) iter1.next();
      Marriage m2 = (Marriage) iter2.next();
      assertEquals(m1, m2);
    }

  }

  /**
   * Asserts that two <code>Date</code> represent the same day
   * (ignores times)
   */
  void assertEquals(Date d1, Date d2) {
    if (d1 == null) {
      assertTrue(d2 == null);
      return;

    } else if (d2 == null) {
      assertTrue(d1 == null);
      return;
    }

    Calendar c1 = Calendar.getInstance();
    c1.setTime(d1);
    Calendar c2 = Calendar.getInstance();
    c2.setTime(d2);

    assertEquals(c1.get(Calendar.DAY_OF_YEAR),
                 c2.get(Calendar.DAY_OF_YEAR));
    assertEquals(c1.get(Calendar.YEAR), c2.get(Calendar.YEAR));
  }

  /**
   * Asserts that two <code>Marriage</code>s are the same
   */
  void assertEquals(Marriage m1, Marriage m2) {
    if (m1 == null) {
      assertTrue(m2 == null);
      return;

    } else if (m2 == null) {
      assertTrue(m1 == null);
      return;
    }

    // To avoid infinite recursion, we compare people using their ids
    assertEquals(m1.getHusband().getId(), m2.getHusband().getId());
    assertEquals(m1.getWife().getId(), m2.getWife().getId());
    assertEquals(m1.getDate(), m2.getDate());
    assertEquals(m1.getLocation(), m2.getLocation());
  }

  /**
   * Asserts that two <code>FamilyTree</code>s has the same contents
   */
  void assertEquals(FamilyTree tree1, FamilyTree tree2) {
    assertEquals(tree1.getPeople().size(), tree2.getPeople().size());

    Iterator people = tree1.getPeople().iterator();
    while (people.hasNext()) {
      Person p1 = (Person) people.next();
      Person p2 = tree2.getPerson(p1.getId());
      assertNotNull(p2);
      assertEquals(p1, p2);
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
    assertTrue(message, container.indexOf(containee) != -1);
  }

}
