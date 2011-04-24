package edu.pdx.cs410J.family;

import java.rmi.*;
import java.util.*;

/**
 * <P>This is a concrete class that implements the
 * <code>RemotePerson</code> interface.  It delegates most of its
 * functionality to an underlying {@link Person} object.</P>
 *
 * <P>Note that because this class is only executed on the
 * server-side, it does not have to be public.  However, the fact that
 * it is a <code>UnicastRemoteObject</code> allows the client to
 * execute its methods remotely.</P>
 */
@SuppressWarnings("serial")
class RemotePersonImpl extends java.rmi.server.UnicastRemoteObject
  implements RemotePerson {

  /** The underlying Person that is being modeled */
  private transient Person person;

  /** The remote family tree that created this remote person.  Note
   * this field is transient and is therefore not serialized. */
  private transient RemoteFamilyTree tree;

  ////////////////////  Constructors  ////////////////////////

  /**
   * Creates a new <code>RemotePersonImpl</code> that delegates most
   * of its behavior to a given <code>Person</code>.
   */
  RemotePersonImpl(RemoteFamilyTree tree, Person person) 
    throws RemoteException {

    this.tree = tree;
    this.person = person;
  }

  ////////////////////  Instance Methods  ////////////////////

  public int getId() throws RemoteException {
    return this.person.getId();
  }

  public Person.Gender getGender() throws RemoteException {
    return this.person.getGender();
  }

  public String getFirstName() throws RemoteException {
    return this.person.getFirstName();
  }

  public void setFirstName(String firstName) throws RemoteException {
    this.person.setFirstName(firstName);
  }

  public String getMiddleName() throws RemoteException {
    return this.person.getMiddleName();
  }

  public void setMiddleName(String middleName) 
    throws RemoteException {
    this.person.setMiddleName(middleName);
  }

  public String getLastName() throws RemoteException {
    return this.person.getLastName();
  }

  public void setLastName(String lastName) throws RemoteException {
    this.person.setLastName(lastName);
  }

  public int getFatherId() throws RemoteException {
    return this.person.getFatherId();
  }

  public void setFatherId(int fatherId) throws RemoteException {
    RemotePersonImpl rPerson = 
      (RemotePersonImpl) this.tree.getPerson(fatherId);
    if (rPerson == null) {
      String s = "Could not find person with id " + fatherId;
      throw new FamilyTreeException(s);
    }
    this.person.setFather(rPerson.person);
  }

  public int getMotherId() throws RemoteException {
    return this.person.getMotherId();
  }

  public void setMotherId(int motherId) throws RemoteException {
    RemotePersonImpl rPerson = 
      (RemotePersonImpl) this.tree.getPerson(motherId);
    if (rPerson == null) {
      String s = "Could not find person with id " + motherId;
      throw new FamilyTreeException(s);
    }
    this.person.setMother(rPerson.person);
  }

  public Date getDateOfBirth() throws RemoteException {
    return this.person.getDateOfBirth();
  }

  public void setDateOfBirth(Date dob) throws RemoteException {
    this.person.setDateOfBirth(dob);
  }

  public Date getDateOfDeath() throws RemoteException {
    return this.person.getDateOfDeath();
  }

  public void setDateOfDeath(Date dod) throws RemoteException {
    this.person.setDateOfDeath(dod);
  }

  public String getDescription() throws RemoteException {
    return this.person.toString();
  }

  ////////////////////  Utility Methods  ////////////////////

  /**
   * Two <code>RemotePersonImpl</code>s are considered equal if their
   * underlying persons have the same id.
   */
  public boolean equals(Object o) {
    if (o instanceof RemotePerson) {
      RemotePerson other = (RemotePerson) o;
      try {
        return this.getId() == other.getId();

      } catch (RemoteException ex) {
        return false;
      }
 
    } else {
      return false;
    }
  }

}
