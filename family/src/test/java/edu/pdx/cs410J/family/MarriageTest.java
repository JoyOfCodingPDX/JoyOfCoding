package edu.pdx.cs410J.family;

import org.junit.Test;
import static org.junit.Assert.fail;

/**
 * This class tests the functionality of the <code>Marriage</code>
 * class.
 */
public class MarriageTest extends FamilyTestCase {

  @Test
  public void testMarriage() {
    Person husband = new Person(1, Person.MALE);
    Person wife = new Person(2, Person.FEMALE);
    Marriage m = new Marriage(husband, wife);
    assertEquals(husband, m.getHusband());
    assertEquals(wife, m.getWife());
  }

  @Test
  public void testMarriageHusbandNotMale() {
    Person husband = new Person(1, Person.FEMALE);
    Person wife = new Person(2, Person.FEMALE);
    try {
      new Marriage(husband, wife);
      fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass ...
    }
  }

  @Test
  public void testMarriageWifeNotFemale() {
    Person husband = new Person(1, Person.MALE);
    Person wife = new Person(2, Person.MALE);
    try {
      new Marriage(husband, wife);
      fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass ...
    }
  }

}
