package edu.pdx.cs410E.familyTree;

import edu.pdx.cs410J.familyTree.Person;
import java.rmi.*;
import java.util.Calendar;
import java.util.Date;
import junit.framework.*;

/**
 * This class tests the functionality of implementors of
 * <code>RemotePerson</code>.  Note that it just looks up a
 * <code>RemotePerson</code> in the RMI namespace.  It doesn't pay any
 * matter what the concrete implementation of
 * <code>RemotePerson</code> is.  It is up to the concrete subclass to
 * bind the appropriate <code>RemoteFamilyTree</code> object into the
 * RMI namespace.
 */
public class RemotePersonTest extends RemoteFamilyTreeTest {

  ////////  Constructors

  /**
   * Creates a new <code>RemotePersonTest</code> for running the
   * test of a given name
   */
  public RemotePersonTest(String name) {
    super(name);
  }

  public static Test suite() {
    return(new TestSuite(RemotePersonTest.class));
  }

  /**
   * Run one test method from this class
   */
  public static void main(String[] args) throws Throwable {
    TestSuite suite = new TestSuite();

    if (args.length == 0) {
      suite.addTest(suite());

    } else {
      for (int i = 0; i < args.length; i++) {
        suite.addTest(new RemotePersonTest(args[i]));
      }
    }

    junit.textui.TestRunner.run(suite);
  }

  ////////  Helper methods

  /**
   * Asserts the equality of two dates.  Only takes the month, day,
   * and year into account.
   */
  protected void assertEquals(Date d1, Date d2) {
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(d1);

    Calendar cal2 = Calendar.getInstance();
    cal1.setTime(d2);

    assertEquals(cal1.get(Calendar.DAY_OF_YEAR),
                 cal2.get(Calendar.DAY_OF_YEAR));
    assertEquals(cal1.get(Calendar.YEAR), cal2.get(Calendar.YEAR));
  }

  ////////  Test cases

  public void testGetId() throws RemoteException{
    RemoteFamilyTree tree = getTree();
    assertTrue(tree.createPerson(Person.MALE).getId() >= 0);
  }

  public void testGetGenderMale() throws RemoteException {
    RemotePerson male = getTree().createPerson(Person.MALE);
    assertEquals(Person.MALE, male.getGender());
  }

  public void testGetGenderFemale() throws RemoteException {
    RemotePerson female = getTree().createPerson(Person.FEMALE);
    assertEquals(Person.FEMALE, female.getGender());
  }

  public void testGetFirstName() throws RemoteException {
    RemotePerson person = getTree().createPerson(Person.FEMALE);
    String firstName = "firstName";
    person.setFirstName(firstName);
    assertEquals(firstName, person.getFirstName());
  }

  public void testGetFirstNameNull() throws RemoteException {
    RemotePerson person = getTree().createPerson(Person.FEMALE);
    assertNull(person.getFirstName());
  }

  public void testGetMiddleName() throws RemoteException {
    RemotePerson person = getTree().createPerson(Person.FEMALE);
    String middleName = "middleName";
    person.setMiddleName(middleName);
    assertEquals(middleName, person.getMiddleName());
  }

  public void testGetMiddleNameNull() throws RemoteException {
    RemotePerson person = getTree().createPerson(Person.FEMALE);
    assertNull(person.getMiddleName());
  }

  public void testGetLastName() throws RemoteException {
    RemotePerson person = getTree().createPerson(Person.FEMALE);
    String lastName = "lastName";
    person.setLastName(lastName);
    assertEquals(lastName, person.getLastName());
  }

  public void testGetLastNameNull() throws RemoteException {
    RemotePerson person = getTree().createPerson(Person.FEMALE);
    assertNull(person.getLastName());
  }

  public void testGetFatherId() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    RemotePerson father = tree.createPerson(Person.MALE);
    person.setFatherId(father.getId());

    assertEquals(father.getId(), person.getFatherId());
  }

  public void testSetFatherIdBadId() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    try {
      person.setFatherId(40000);
      fail("Should have thrown IllegalARgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  public void testSetFatherIdNotMale() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    RemotePerson father = tree.createPerson(Person.FEMALE);

    try {
      person.setFatherId(father.getId());
      fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  public void testGetMotherId() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    RemotePerson mother = tree.createPerson(Person.FEMALE);
    person.setMotherId(mother.getId());

    assertEquals(mother.getId(), person.getMotherId());
  }

  public void testSetMotherIdBadId() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    try {
      person.setMotherId(40000);
      fail("Should have thrown IllegalARgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  public void testSetMotherIdNotFemale() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    RemotePerson mother = tree.createPerson(Person.MALE);

    try {
      person.setMotherId(mother.getId());
      fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  public void testSetDateOfBirth() throws RemoteException {
    Date dob = new Date();
    RemotePerson person = getTree().createPerson(Person.MALE);
    person.setDateOfBirth(dob);
    assertEquals(dob, person.getDateOfBirth());
  }

  public void testSetDateOfDeath() throws RemoteException {
    Date dob = new Date();
    RemotePerson person = getTree().createPerson(Person.MALE);
    person.setDateOfDeath(dob);
    assertEquals(dob, person.getDateOfDeath());
  }

}
