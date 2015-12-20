package edu.pdx.cs410J.family;

import java.rmi.*;
import java.util.Date;

/**
 * This is class implements the <code>RemoteMarriage</code> interface.
 * Basically, it delegates all of its behavior to an underlying {@link
 * edu.pdx.cs410J.family.Marriage} that lives only on the server.
 */
@SuppressWarnings("serial")
class RemoteMarriageImpl extends java.rmi.server.UnicastRemoteObject
  implements RemoteMarriage {

  /** The underlying Marriage that is being modeled. */
  private transient Marriage marriage;

  //////////////////////  Constructors  //////////////////////

  /**
   * Creates a new <code>RemoteMarriageImpl</code> that delegates most
   * of its behavior to a given <code>Marriage</code>
   */
  RemoteMarriageImpl(Marriage marriage) throws RemoteException {
    this.marriage = marriage;
  }

  ////////////////////  Instance Methods  ////////////////////

  public int getHusbandId() throws RemoteException {
    return this.marriage.getHusband().getId();
  }

  public int getWifeId() throws RemoteException {
    return this.marriage.getWife().getId();
  }

  public Date getDate() throws RemoteException {
    return this.marriage.getDate();
  }

  public void setDate(Date date) throws RemoteException {
    this.marriage.setDate(date);
  }

  public String getLocation() throws RemoteException {
    return this.marriage.getLocation();
  }

  public void setLocation(String location) throws RemoteException {
    this.marriage.setLocation(location);
  }

  public String getDescription() throws RemoteException {
    return this.marriage.toString();
  }

}
