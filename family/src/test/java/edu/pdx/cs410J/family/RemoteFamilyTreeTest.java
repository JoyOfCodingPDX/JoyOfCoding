package edu.pdx.cs410J.family;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * This class tests the functionality of the various implementation of
 * <code>RemoteFamilyTree</code>.
 */
public class RemoteFamilyTreeTest extends RemoteTestCase {

  @Test
  public void testCreatePerson() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    tree.createPerson(Person.MALE);
  }

  @Test
  public void testCreatePersonBadGender() throws RemoteException {
    try {
      RemoteFamilyTree tree = getTree();
      tree.createPerson(Person.Gender.UNKNOWN);
      Assertions.fail("Should have thrown a FamilyTreeException");

    } catch (FamilyTreeException ex) {
      // pass...
    }
  }

  @Test
  public void testGetPersonById() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    int id = person.getId();
    RemotePerson person2 = tree.getPerson(id);
    assertNotNull(person2);
    Assertions.assertEquals(person, person2);
  }

  @Test
  public void testGetNonexistingPersonById() throws RemoteException {
    assertNull(getTree().getPerson(-79));
  }

  @Test
  public void testGetPersonByName() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    String firstName = "Test";
    String lastName = "Person" + System.currentTimeMillis();
    person.setFirstName(firstName);
    person.setLastName(lastName);

    RemotePerson person2 = tree.getPerson(firstName, lastName);
    assertNotNull(person2);
    Assertions.assertEquals(person, person2);
  }

  @Test
  public void testGetPersonSameNameTwice() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    String firstName = "Test";
    String lastName = "Person" + System.currentTimeMillis();
    person.setFirstName(firstName);
    person.setLastName(lastName);

    RemotePerson person2 = tree.createPerson(Person.MALE);
    person2.setFirstName(firstName);
    person2.setLastName(lastName);

    try {
      tree.getPerson(firstName, lastName);
      Assertions.fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }

  }

  @Test
  public void testGetNonexistingPersonByName()
    throws RemoteException {
    assertNull(getTree().getPerson("No such", "Person"));
  }

  ////////  RemoteFamilyTree.createMarriage()

    @Test
  public void testCreateMarriage() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson husband = tree.createPerson(Person.MALE);
    RemotePerson wife = tree.createPerson(Person.FEMALE);
    RemoteMarriage marriage = 
      tree.createMarriage(husband.getId(), wife.getId());
    assertNotNull(marriage);
  }

    @Test
  public void testCreateMarriageNoSuchHusband() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson wife = tree.createPerson(Person.FEMALE);
    try {
      tree.createMarriage(4444, wife.getId());
      Assertions.fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

    @Test
  public void testCreateMarriageNoSuchWife() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson husband = tree.createPerson(Person.MALE);
    try {
      tree.createMarriage(husband.getId(), 4444);
      Assertions.fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

    @Test
  public void testCreateMarriageHusbandNotMale() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson husband = tree.createPerson(Person.FEMALE);
    RemotePerson wife = tree.createPerson(Person.FEMALE);

    try {
      tree.createMarriage(husband.getId(), wife.getId());
      Assertions.fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

    @Test
  public void testCreateMarriageWifeNotFemale() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson husband = tree.createPerson(Person.MALE);
    RemotePerson wife = tree.createPerson(Person.MALE);

    try {
      tree.createMarriage(husband.getId(), wife.getId());
      Assertions.fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  ////////  RemoteFamilyTree.getMarriage()

    @Test
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
    Assertions.assertEquals(marriage, marriage2);
  }

  public void testGetMarriageNoSuchHusband() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson wife = tree.createPerson(Person.FEMALE);
    try {
      tree.getMarriage(4444, wife.getId());
      Assertions.fail("Should have thrown IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

    @Test
  public void testGetMarriageNoSuchWife() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson husband = tree.createPerson(Person.MALE);
    try {
      tree.getMarriage(husband.getId(), 4444);
      Assertions.fail("Should have thrown IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

    @Test
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
