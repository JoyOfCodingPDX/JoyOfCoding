package edu.pdx.cs399J.family.tests;

import edu.pdx.cs399J.family.*;
import java.util.*;
import junit.framework.*;

/**
 * This class tests the functionality of the <code>Person</code> class.
 */
public class PersonTest extends TestCase {

  /**
   * Returns a suite containing all of the tests in this class
   */
  public static Test suite() {
    return(new TestSuite(PersonTest.class));
  }

  /**
   * Creates a new <code>PersonTest</code> for running the test of a
   * given name
   */
  public PersonTest(String name) {
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
        suite.addTest(new PersonTest(args[i]));
      }
    }

    junit.textui.TestRunner.run(suite);
  }

  ////////  Test cases

  /**
   * Create a person with an invalid id.  Make sure it throws an
   * IllegalArgumentException. 
   */
  public void testInvalidPersonId() {
    try {
      Person p = new Person(-7, Person.FEMALE);
      fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  /**
   * Create a person with an invalid gender.  Make sure it throws an
   * IllegalArgumentException. 
   */
  public void testInvalidGender() {
    try {
      Person p = new Person(1, 3);
      fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  public void testGetId() {
    int id = 4;
    Person p = new Person(id, Person.MALE);
    assertEquals(id, p.getId());
  }

  public void testGetGenderMale() {
    int gender = Person.MALE;
    Person p = new Person(4, gender);
    assertEquals(gender, p.getGender());
  }

  public void testGetGenderFemale() {
    int gender = Person.FEMALE;
    Person p = new Person(4, gender);
    assertEquals(gender, p.getGender());
  }

  public void testFirstName() {
    String name = "Bob";
    Person p = new Person(4, Person.MALE);
    p.setFirstName(name);
    assertEquals(name, p.getFirstName());
  }

  public void testLastName() {
    String name = "Bob";
    Person p = new Person(4, Person.MALE);
    p.setLastName(name);
    assertEquals(name, p.getLastName());
  }

  public void testMiddleName() {
    String name = "Bob";
    Person p = new Person(4, Person.MALE);
    p.setMiddleName(name);
    assertEquals(name, p.getMiddleName());
  }

  public void testFullName() {
    String first = "First";
    String middle = "Middle";
    String last = "Last";

    Person p = new Person(3, Person.FEMALE);
    p.setFirstName(first);
    p.setMiddleName(middle);
    p.setLastName(last);

    String full = first + " " + middle + " " + last;
    assertEquals(full, p.getFullName());
  }

  public void testFather() {
    int id = 2;
    Person child = new Person(1, Person.FEMALE);
    Person father = new Person(id, Person.MALE);
    child.setFather(father);
    assertEquals(father, child.getFather());
    assertEquals(id, child.getFatherId());
  }

  public void testFemaleFather() {
    Person child = new Person(1, Person.FEMALE);
    Person father = new Person(2, Person.FEMALE);
    try {
      child.setFather(father);
      fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  public void testGetFatherIdUnknownFather() {
    Person child = new Person(1, Person.FEMALE);
    assertEquals(Person.UNKNOWN, child.getFatherId());
  }

  public void testMother() {
    int id = 2;
    Person child = new Person(1, Person.FEMALE);
    Person mother = new Person(id, Person.FEMALE);
    child.setMother(mother);
    assertEquals(mother, child.getMother());
    assertEquals(id, child.getMotherId());
  }

  public void testMaleMother() {
    Person child = new Person(1, Person.FEMALE);
    Person mother = new Person(2, Person.MALE);
    try {
      child.setMother(mother);
      fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  public void testGetMotherIdUnknownMother() {
    Person child = new Person(1, Person.FEMALE);
    assertEquals(Person.UNKNOWN, child.getMotherId());
  }

  public void testDateOfBirth() {
    Date dob = new Date(123456L);
    Person p = new Person(1, Person.FEMALE);
    p.setDateOfBirth(dob);
    assertEquals(dob, p.getDateOfBirth());
  }

  public void testDateOfDeath() {
    Date dod = new Date(123456L);
    Person p = new Person(1, Person.FEMALE);
    p.setDateOfDeath(dod);
    assertEquals(dod, p.getDateOfDeath());
  }

  public void testDateOfDeathBeforeDateOfBirth() {
    Date dob = new Date(120000L);
    Date dod = new Date(100000L);
    Person p = new Person(1, Person.MALE);
    p.setDateOfBirth(dob);
    try {
      p.setDateOfDeath(dod);
      fail("Should have thrown IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  public void testAddMarriage() {
    Person husband = new Person(1, Person.MALE);
    Person wife = new Person(2, Person.FEMALE);
    Marriage m = new Marriage(husband, wife);
    husband.addMarriage(m);
    assertTrue(husband.getMarriages().contains(m));
  }

  public void testMaleNotInMarriage() {
    Person husband = new Person(1, Person.MALE);
    Person wife = new Person(2, Person.FEMALE);
    Marriage m = new Marriage(husband, wife);

    Person p = new Person(3, Person.MALE);
    try {
      p.addMarriage(m);
      fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  public void testFemaleNotInMarriage() {
    Person husband = new Person(1, Person.MALE);
    Person wife = new Person(2, Person.FEMALE);
    Marriage m = new Marriage(husband, wife);

    Person p = new Person(3, Person.FEMALE);
    try {
      p.addMarriage(m);
      fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  public void testEquals() {
    Person p1 = new Person(1, Person.MALE);
    Person p2 = new Person(1, Person.MALE);
    assertEquals(p1, p2);
  }

  public void testNotEquals() {
    Person p1 = new Person(1, Person.MALE);
    Person p2 = new Person(2, Person.MALE);
    assertTrue(!p1.equals(p2));
  }

}
