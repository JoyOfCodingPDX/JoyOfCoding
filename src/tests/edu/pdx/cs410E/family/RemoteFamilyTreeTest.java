package edu.pdx.cs410E.familyTree;

import edu.pdx.cs410J.familyTree.Person;
import java.rmi.*;
import junit.framework.*;

/**
 * This class tests the functionality of the various implementation of
 * <code>RemoteFamilyTree</code>.
 */
public class RemoteFamilyTreeTest extends RemoteTest {

  /**
   * Creates a new <code>RemoteFamilyTreeTest</code> for running the
   * test of a given name
   */
  public RemoteFamilyTreeTest(String name) {
    super(name);
  }

  ////////  Test methods

  public void testCreatePerson() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
  }

  public void testCreatePersonBadGender() throws RemoteException {
    try {
      RemoteFamilyTree tree = getTree();
      tree.createPerson(42);
      fail("Should have thrown IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  public void testGetPersonById() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    int id = person.getId();
    RemotePerson person2 = tree.getPerson(id);
    assertNotNull(person2);
    assertEquals(person, person2);
  }

  public void testGetNonexistingPersonById() throws RemoteException {
    assertNull(getTree().getPerson(-79));
  }

  public void testGetPersonByName() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    String firstName = "Test";
    String lastName = "Person" + System.currentTimeMillis();;
    person.setFirstName(firstName);
    person.setLastName(lastName);

    RemotePerson person2 = tree.getPerson(firstName, lastName);
    assertNotNull(person2);
    assertEquals(person, person2);
  }

  public void testGetPersonSameNameTwice() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    String firstName = "Test";
    String lastName = "Person" + System.currentTimeMillis();;
    person.setFirstName(firstName);
    person.setLastName(lastName);

    RemotePerson person2 = tree.createPerson(Person.MALE);
    person2.setFirstName(firstName);
    person2.setLastName(lastName);

    try {
      tree.getPerson(firstName, lastName);
      fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }

  }

  public void testGetNonexistingPersonByName()
    throws RemoteException {
    assertNull(getTree().getPerson("No such", "Person"));
  }

  ////////  RemoteFamilyTree.createMarriage()

  public void testCreateMarriage() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson husband = tree.createPerson(Person.MALE);
    RemotePerson wife = tree.createPerson(Person.FEMALE);
    RemoteMarriage marriage = 
      tree.createMarriage(husband.getId(), wife.getId());
    assertNotNull(marriage);
  }

  public void testCreateMarriageNoSuchHusband() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson wife = tree.createPerson(Person.FEMALE);
    RemoteMarriage marriage = 
      tree.createMarriage(4444, wife.getId());
    assertNotNull(marriage);
  }

  public void testCreateMarriageNoSuchWife() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson husband = tree.createPerson(Person.MALE);
    RemoteMarriage marriage = 
      tree.createMarriage(husband.getId(), 4444);
    assertNotNull(marriage);
  }

  public void testCreateMarriageHusbandNotMale() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson husband = tree.createPerson(Person.FEMALE);
    RemotePerson wife = tree.createPerson(Person.FEMALE);

    try {
      tree.createMarriage(husband.getId(), wife.getId());
      fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  public void testCreateMarriageWifeNotFemale() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson husband = tree.createPerson(Person.MALE);
    RemotePerson wife = tree.createPerson(Person.MALE);

    try {
      tree.createMarriage(husband.getId(), wife.getId());
      fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  ////////  RemoteFamilyTree.getMarriage()

  public void testGetExistingMarriage() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson husband = tree.createPerson(Person.MALE);
    RemotePerson wife = tree.createPerson(Person.FEMALE);
    RemoteMarriage marriage = 
      tree.createMarriage(husband.getId(), wife.getId());
    assertNotNull(marriage);

    RemoteMarriage marriage2 = 
      tree.getMarriage(husband.getId(), wife.getId());
    assertNotNull(marriage2);
    assertEquals(marriage, marriage2);
  }

  public void testGetMarriageNoSuchHusband() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson wife = tree.createPerson(Person.FEMALE);
    try {
      tree.getMarriage(4444, wife.getId());
      fail("Should have thrown IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  public void testGetMarriageNoSuchWife() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson husband = tree.createPerson(Person.MALE);
    try {
      tree.getMarriage(husband.getId(), 4444);
      fail("Should have thrown IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  public void testGetMarriageNeverBeenMarried() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson husband = tree.createPerson(Person.MALE);
    RemotePerson wife = tree.createPerson(Person.FEMALE);
    try {
      tree.getMarriage(husband.getId(), wife.getId());

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

}
