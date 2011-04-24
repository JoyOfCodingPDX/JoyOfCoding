package edu.pdx.cs410J.family;

import java.io.*;
import java.rmi.*;
import java.util.*;

/**
 * This interface specifies a factory that is responsible for creating,
 * storing and querying {@link Person} objects.
 */
public interface RemoteFamilyTree extends Remote {

  /**
   * Creates a new <code>Person</code> of a given gender
   *
   * @throws FamilyTreeException
   *         If <code>gender</code> is neither {@link Person#MALE} nor
   *         {@link Person#FEMALE}.
   */
  public RemotePerson createPerson(Person.Gender gender)
    throws RemoteException;

  /**
   * Gets the person with the given id.  If no person with that id
   * exists, then <code>null</code> is returned.
   */
  public RemotePerson getPerson(int id) throws RemoteException;

  /**
   * Gets the person with the given first and last name.  If no person
   * with that name is found, then <code>null</code> is returned.
   *
   * @throws IllegalArgumentException
   *         If more than one person in the family tree has that
   *         name. 
   */
  public RemotePerson getPerson(String firstName, String lastName)
    throws RemoteException;

  /**
   * Shuts down this <code>PersonFactory</code>.  Modified
   * <code>Person</code>s are written to persistent storage as
   * appropriate.
   */
  public void shutdown() throws IOException, RemoteException;

  /**
   * Returns the marriage between two people.  If the two people have
   * never been married, then <code>null</code> is returned.
   *
   * @throws IllegalArgumentException
   *         If no person with husbandId or no person with wifeId
   *         exists in the family tree
   */
  public RemoteMarriage getMarriage(int husbandId, int wifeId) 
    throws RemoteException; 

  /**
   * Creates a new marriage between two people
   * 
   * @throws IllegalArgumentException
   *         If no person with husbandId or no person with wifeId
   *         exists in the family tree or if either spouse does not
   *         have the proper gender.
   */
  public RemoteMarriage createMarriage(int husbandId, int wifeId) 
    throws RemoteException;

  /**
   * Returns the people in the family tree that are living
   * (i&#46;e&#46; have a date of birth, but no date of death)
   */
  public Collection<RemotePerson> getLiving() throws RemoteException;

  /**
   * Returns the people in the family tree were alive at a certain
   * time 
   */
  public Collection<RemotePerson> getLiving(Date date) throws RemoteException;

}
