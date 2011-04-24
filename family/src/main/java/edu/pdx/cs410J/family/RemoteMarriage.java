package edu.pdx.cs410J.family;

import java.rmi.*;
import java.util.*;

/**
 * This interface models a {@link edu.pdx.cs410J.family.Marriage}
 * that is accessed remotely using Java Remote Method Invocation.
 */
public interface RemoteMarriage extends Remote {

  /**
   * Returns the id of the husband in the marriage
   */
  public int getHusbandId() throws RemoteException;

  /**
   * Returns the id of the wife in the marriage
   */
  public int getWifeId() throws RemoteException;

  /**
   * Returns the date on which the husband and wife were married
   */
  public Date getDate() throws RemoteException;

  /**
   * Sets the date on which the husband and wife were married
   */
  public void setDate(Date date) throws RemoteException;

  /**
   * Returns the location at which the husband and wife were married
   */
  public String getLocation() throws RemoteException;

  /**
   * Sets the location at which the husband and wife were married
   */
  public void setLocation(String location) throws RemoteException;

  /**
   * Returns a textual description of this marriage
   */
  public String getDescription() throws RemoteException;

}
