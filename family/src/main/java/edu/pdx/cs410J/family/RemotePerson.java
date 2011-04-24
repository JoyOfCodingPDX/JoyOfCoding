package edu.pdx.cs410J.family;

import java.rmi.*;
import java.util.*;

/**
 * This interface models a {@link edu.pdx.cs410J.family.Person}
 * that is accessed remotely using Java Remote Method Invocation.
 */
public interface RemotePerson extends Remote {

  /**
   * Returns this person's unique id
   */
  public int getId() throws RemoteException;

  /**
   * Returns this person's gender (either {@link
   * edu.pdx.cs410J.family.Person#MALE} or
   * {@link edu.pdx.cs410J.family.Person#FEMALE}.
   */
  public Person.Gender getGender() throws RemoteException;

//   /**
//    * Sets this person's gender
//    */
//   public void setGender(int gender) throws RemoteException;

  /**
   * Returns this person's first name
   */
  public String getFirstName() throws RemoteException;

  /**
   * Sets this person's first name
   */
  public void setFirstName(String firstName) throws RemoteException;

  /**
   * Returns this person's middle name
   */
  public String getMiddleName() throws RemoteException;

  /**
   * Sets this person's middle name
   */
  public void setMiddleName(String middleName) throws RemoteException;

  /**
   * Returns this person's last name
   */
  public String getLastName() throws RemoteException;

  /**
   * Sets this person's last name
   */
  public void setLastName(String lastName) throws RemoteException;

  /**
   * Returns the id of this person's father
   */
  public int getFatherId() throws RemoteException;

  /**
   * Sets the id of this person's father
   *
   * @throws FamilyTreeException
   *        The person with the given id cannot be found or the person
   *        with that id is not {@link Person#MALE}
   */
  public void setFatherId(int fatherId) throws RemoteException;

  /**
   * Returns the id of this person's mother
   */
  public int getMotherId() throws RemoteException;

  /**
   * Sets the id of this person's mother
   *
   * @throws FamilyTreeException
   *        The person with the given id cannot be found or the person
   *        with that id is not {@link Person#FEMALE}
   */
  public void setMotherId(int motherId) throws RemoteException;

//   /**
//    * Returns the marriages that this person has been involved in
//    */
//   public Collection getMarriages() throws RemoteException;

  /**
   * Returns this person's date of birth
   */
  public Date getDateOfBirth() throws RemoteException;

  /**
   * Sets this person's date of birth
   */
  public void setDateOfBirth(Date dob) throws RemoteException;

  /**
   * Returns this person's date of death
   */
  public Date getDateOfDeath() throws RemoteException;

  /**
   * Sets this person's date of death
   */
  public void setDateOfDeath(Date dod) throws RemoteException;

  /**
   * Returns a string describing this person
   */
  public String getDescription() throws RemoteException;

}

