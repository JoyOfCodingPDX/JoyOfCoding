package edu.pdx.cs410E.familyTree;

import java.rmi.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;  // Not to be confused with java.sql.Date

/**
 * This class implements <code>RemoteMarriage</code> and models a
 * marriage whose data is stored in a relational database.
 */
class RdbRemoteMarriage extends java.rmi.server.UnicastRemoteObject
  implements RemoteMarriage {

  /** A connection to the relational database */
  private transient Connection conn;

  /** The id of the husband in the marriage */
  private int husbandId;

  /** The id of the wife in the marriage */
  private int wifeId;

  ///////////////////////  Constructors  ///////////////////////

  /**
   * Creates a new <code>RdbRemoteMarriage</code> between the husband
   * and wife with the given ids.  The given connection will be used
   * to contact the relational database.
   */
  RdbRemoteMarriage(int husbandId, int wifeId, Connection conn) 
    throws RemoteException {

    this.husbandId = husbandId;
    this.wifeId = wifeId;
    this.conn = conn;
  }

  /////////////////////  Instance Methods  /////////////////////

  public int getHusbandId() throws RemoteException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  public int getWifeId() throws RemoteException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  public Date getDate() throws RemoteException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  public void setDate(Date date) throws RemoteException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  public String getLocation() throws RemoteException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  public void setLocation(String location) throws RemoteException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  public String getDescription() throws RemoteException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

}
