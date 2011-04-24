package edu.pdx.cs410J.family;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

import java.util.Date;

/**
 * This class tests the functionality of the <code>Person</code> class.
 */
public class PersonTest {

  /**
   * Create a person with an invalid id.  Make sure it throws an
   * FamilyTreeException. 
   */
  @Test
  public void testInvalidPersonId() {
    try {
      new Person(-7, Person.FEMALE);
      fail("Should have thrown an FamilyTreeException");

    } catch (FamilyTreeException ex) {
      // pass...
    }
  }

  /**
   * Create a person with an invalid gender.  Make sure it throws an
   * FamilyTreeException. 
   */
  @Test
  public void testInvalidGender() {
    try {
      new Person(1, Person.Gender.UNKNOWN);
      fail("Should have thrown an FamilyTreeException");

    } catch (FamilyTreeException ex) {
      // pass...
    }
  }

  @Test
  public void testGetId() {
    int id = 4;
    Person p = new Person(id, Person.MALE);
    assertEquals(id, p.getId());
  }

  @Test
  public void testGetGenderMale() {
    Person.Gender gender = Person.MALE;
    Person p = new Person(4, gender);
    assertEquals(gender, p.getGender());
  }

  @Test
  public void testGetGenderFemale() {
    Person.Gender gender = Person.FEMALE;
    Person p = new Person(4, gender);
    assertEquals(gender, p.getGender());
  }

  @Test
  public void testFirstName() {
    String name = "Bob";
    Person p = new Person(4, Person.MALE);
    p.setFirstName(name);
    assertEquals(name, p.getFirstName());
  }

  @Test
  public void testLastName() {
    String name = "Bob";
    Person p = new Person(4, Person.MALE);
    p.setLastName(name);
    assertEquals(name, p.getLastName());
  }

  @Test
  public void testMiddleName() {
    String name = "Bob";
    Person p = new Person(4, Person.MALE);
    p.setMiddleName(name);
    assertEquals(name, p.getMiddleName());
  }

  @Test
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

  @Test
  public void testFather() {
    int id = 2;
    Person child = new Person(1, Person.FEMALE);
    Person father = new Person(id, Person.MALE);
    child.setFather(father);
    assertEquals(father, child.getFather());
    assertEquals(id, child.getFatherId());
  }

  @Test
  public void testFemaleFather() {
    Person child = new Person(1, Person.FEMALE);
    Person father = new Person(2, Person.FEMALE);
    try {
      child.setFather(father);
      fail("Should have thrown an FamilyTreeException");

    } catch (FamilyTreeException ex) {
      // pass...
    }
  }

  @Test
  public void testGetFatherIdUnknownFather() {
    Person child = new Person(1, Person.FEMALE);
    assertEquals(Person.UNKNOWN, child.getFatherId());
  }

  @Test
  public void testMother() {
    int id = 2;
    Person child = new Person(1, Person.FEMALE);
    Person mother = new Person(id, Person.FEMALE);
    child.setMother(mother);
    assertEquals(mother, child.getMother());
    assertEquals(id, child.getMotherId());
  }

  @Test
  public void testMaleMother() {
    Person child = new Person(1, Person.FEMALE);
    Person mother = new Person(2, Person.MALE);
    try {
      child.setMother(mother);
      fail("Should have thrown an FamilyTreeException");

    } catch (FamilyTreeException ex) {
      // pass...
    }
  }

  @Test
  public void testGetMotherIdUnknownMother() {
    Person child = new Person(1, Person.FEMALE);
    assertEquals(Person.UNKNOWN, child.getMotherId());
  }

  @Test
  public void testDateOfBirth() {
    Date dob = new Date(123456L);
    Person p = new Person(1, Person.FEMALE);
    p.setDateOfBirth(dob);
    assertEquals(dob, p.getDateOfBirth());
  }

  @Test
  public void testDateOfDeath() {
    Date dod = new Date(123456L);
    Person p = new Person(1, Person.FEMALE);
    p.setDateOfDeath(dod);
    assertEquals(dod, p.getDateOfDeath());
  }

  @Test
  public void testDateOfDeathBeforeDateOfBirth() {
    Date dob = new Date(120000L);
    Date dod = new Date(100000L);
    Person p = new Person(1, Person.MALE);
    p.setDateOfBirth(dob);
    try {
      p.setDateOfDeath(dod);
      fail("Should have thrown FamilyTreeException");

    } catch (FamilyTreeException ex) {
      // pass...
    }
  }

  @Test
  public void testAddMarriage() {
    Person husband = new Person(1, Person.MALE);
    Person wife = new Person(2, Person.FEMALE);
    Marriage m = new Marriage(husband, wife);
    husband.addMarriage(m);
    assertTrue(husband.getMarriages().contains(m));
  }

  @Test
  public void testMaleNotInMarriage() {
    Person husband = new Person(1, Person.MALE);
    Person wife = new Person(2, Person.FEMALE);
    Marriage m = new Marriage(husband, wife);

    Person p = new Person(3, Person.MALE);
    try {
      p.addMarriage(m);
      fail("Should have thrown an FamilyTreeException");

    } catch (FamilyTreeException ex) {
      // pass...
    }
  }

  @Test
  public void testFemaleNotInMarriage() {
    Person husband = new Person(1, Person.MALE);
    Person wife = new Person(2, Person.FEMALE);
    Marriage m = new Marriage(husband, wife);

    Person p = new Person(3, Person.FEMALE);
    try {
      p.addMarriage(m);
      fail("Should have thrown an FamilyTreeException");

    } catch (FamilyTreeException ex) {
      // pass...
    }
  }

  @Test
  public void testEquals() {
    Person p1 = new Person(1, Person.MALE);
    Person p2 = new Person(1, Person.MALE);
    assertEquals(p1, p2);
  }

  @Test
  public void testNotEquals() {
    Person p1 = new Person(1, Person.MALE);
    Person p2 = new Person(2, Person.MALE);
    assertTrue(!p1.equals(p2));
  }

}
