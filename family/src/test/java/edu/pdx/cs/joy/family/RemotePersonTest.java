package edu.pdx.cs.joy.family;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.Date;

/**
 * This class tests the functionality of implementors of
 * <code>RemotePerson</code>.  Note that it just looks up a
 * <code>RemotePerson</code> in the RMI namespace.  It doesn't pay any
 * matter what the concrete implementation of
 * <code>RemotePerson</code> is.
 */
public class RemotePersonTest extends RemoteTestCase {

  @Test
  public void testGetId() throws RemoteException{
    RemoteFamilyTree tree = getTree();
    assertTrue(tree.createPerson(Person.MALE).getId() >= 0);
  }

  @Test
  public void testGetGenderMale() throws RemoteException {
    RemotePerson male = getTree().createPerson(Person.MALE);
    Assertions.assertEquals(Person.MALE, male.getGender());
  }

  @Test
  public void testGetGenderFemale() throws RemoteException {
    RemotePerson female = getTree().createPerson(Person.FEMALE);
    Assertions.assertEquals(Person.FEMALE, female.getGender());
  }

  @Test
  public void testGetFirstName() throws RemoteException {
    RemotePerson person = getTree().createPerson(Person.FEMALE);
    String firstName = "firstName";
    person.setFirstName(firstName);
    Assertions.assertEquals(firstName, person.getFirstName());
  }

  @Test
  public void testGetFirstNameNull() throws RemoteException {
    RemotePerson person = getTree().createPerson(Person.FEMALE);
    assertNull(person.getFirstName());
  }

  @Test
  public void testGetMiddleName() throws RemoteException {
    RemotePerson person = getTree().createPerson(Person.FEMALE);
    String middleName = "middleName";
    person.setMiddleName(middleName);
    Assertions.assertEquals(middleName, person.getMiddleName());
  }

  @Test
  public void testGetMiddleNameNull() throws RemoteException {
    RemotePerson person = getTree().createPerson(Person.FEMALE);
    assertNull(person.getMiddleName());
  }

  @Test
  public void testGetLastName() throws RemoteException {
    RemotePerson person = getTree().createPerson(Person.FEMALE);
    String lastName = "lastName";
    person.setLastName(lastName);
    Assertions.assertEquals(lastName, person.getLastName());
  }

  @Test
  public void testGetLastNameNull() throws RemoteException {
    RemotePerson person = getTree().createPerson(Person.FEMALE);
    assertNull(person.getLastName());
  }

  @Test
  public void testGetFatherId() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    RemotePerson father = tree.createPerson(Person.MALE);
    person.setFatherId(father.getId());

    Assertions.assertEquals(father.getId(), person.getFatherId());
  }

  @Test
  public void testSetFatherIdBadId() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    try {
      person.setFatherId(40000);
      Assertions.fail("Should have thrown FamilyTreeException");

    } catch (FamilyTreeException ex) {
      // pass...
    }
  }

  @Test
  public void testSetFatherIdNotMale() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    RemotePerson father = tree.createPerson(Person.FEMALE);

    try {
      person.setFatherId(father.getId());
      Assertions.fail("Should have thrown an FamilyTreeException");

    } catch (FamilyTreeException ex) {
      // pass...
    }
  }

  @Test
  public void testGetMotherId() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    RemotePerson mother = tree.createPerson(Person.FEMALE);
    person.setMotherId(mother.getId());

    Assertions.assertEquals(mother.getId(), person.getMotherId());
  }

  @Test
  public void testSetMotherIdBadId() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    try {
      person.setMotherId(40000);
      Assertions.fail("Should have thrown FamilyTreeException");

    } catch (FamilyTreeException ex) {
      // pass...
    }
  }

  @Test
  public void testSetMotherIdNotFemale() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    RemotePerson mother = tree.createPerson(Person.MALE);

    try {
      person.setMotherId(mother.getId());
      Assertions.fail("Should have thrown an FamilyTreeException");

    } catch (FamilyTreeException ex) {
      // pass...
    }
  }

  @Test
  public void testSetDateOfBirth() throws RemoteException {
    Date dob = new Date();
    RemotePerson person = getTree().createPerson(Person.MALE);
    person.setDateOfBirth(dob);
    assertEquals(dob, person.getDateOfBirth());
  }

  @Test
  public void testSetDateOfDeath() throws RemoteException {
    Date dob = new Date();
    RemotePerson person = getTree().createPerson(Person.MALE);
    person.setDateOfDeath(dob);
    assertEquals(dob, person.getDateOfDeath());
  }

}
