package edu.pdx.cs410E.familyTree;

import java.rmi.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;  // Not to be confused with java.sql.Date

/**
 * This class implements <code>RemotePerson</code> and models a person
 * whose data is stored in a relational database.  Note that no {@link
 * edu.pdx.cs410J.familyTree.Person} object is created.
 */
class RdbRemotePerson extends java.rmi.server.UnicastRemoteObject
  implements RemotePerson {

  /** The id of the person being modeled */
  private transient int id;

  /** The connection to the database through which the person's data
   * is accessed */
  private transient Connection conn;

  ///////////////////////  Constructors  ///////////////////////

  /**
   * Creates a new <code>RdbRemotePerson</code> for a person with a
   * given id.  The given connection will be used to contact the
   * relational database.
   */
  RdbRemotePerson(int id, Connection conn) throws RemoteException {
    this.id = id;
    this.conn = conn;
  }

  /////////////////////  Instance Methods  //////////////////////

  public int getId() throws RemoteException {
    return this.id;
  }

  public String getFirstName() throws RemoteException {
    try {
      Statement stmt = this.conn.createStatement();
      String s =
        "SELECT first_name FROM people WHERE id = " + this.id;
      ResultSet rs = stmt.executeQuery(s);
      rs.next();
      return rs.getString(1);

    } catch (SQLException ex) {
      String s = "While getting first name of " + this.id;
      throw new RemoteException(s, ex);
    }
  }

  public void setFirstName(String firstName) throws RemoteException {
    try {
      Statement stmt = this.conn.createStatement();
      String s = 
        "UPDATE people SET first_name = '" + firstName + 
        "' WHERE id = " + this.id;
      stmt.executeUpdate(s);

    } catch (SQLException ex) {
      String s = "While setting first name of " + this.id + " to \"" +
        firstName + "\": " + ex;
      throw new RemoteException(s, ex);
    }
  }

  public String getMiddleName() throws RemoteException {
    throw new UnsupportedOperationException("getMiddleName() Not implemented yet");
  }

  public void setMiddleName(String middleName) 
    throws RemoteException {
    throw new UnsupportedOperationException("setMiddleName() Not implemented yet");
  }

  public String getLastName() throws RemoteException {
    throw new UnsupportedOperationException("getLastName() Not implemented yet");
  }

  public void setLastName(String lastName) throws RemoteException {
    try {
      Statement stmt = this.conn.createStatement();
      String s = 
        "UPDATE people SET last_name = '" + lastName + 
        "' WHERE id = " + this.id;
      stmt.executeUpdate(s);

    } catch (SQLException ex) {
      String s = "While setting last name of " + this.id + " to \"" +
        lastName + "\": " + ex;
      throw new RemoteException(s, ex);
    }
  }

  public int getFatherId() throws RemoteException {
    throw new UnsupportedOperationException("getFatherId() Not implemented yet");
  }

  public void setFatherId(int fatherId) throws RemoteException {
    throw new UnsupportedOperationException("setFatherId() Not implemented yet");
  }

  public int getMotherId() throws RemoteException {
    throw new UnsupportedOperationException("getMotherId() Not implemented yet");
  }

  public void setMotherId(int motherId) throws RemoteException {
    throw new UnsupportedOperationException("setMotherId() Not implemented yet");
  }

  public Date getDateOfBirth() throws RemoteException {
    throw new UnsupportedOperationException("getDob() Not implemented yet");
  }

  public void setDateOfBirth(Date dob) throws RemoteException {
    throw new UnsupportedOperationException("setDob() Not implemented yet");
  }

  public Date getDateOfDeath() throws RemoteException {
    throw new UnsupportedOperationException("getDod() Not implemented yet");
  }

  public void setDateOfDeath(Date dod) throws RemoteException {
    throw new UnsupportedOperationException("setDod() Not implemented yet");
  }

  /**
   * Helper method that returns the full name of a person with a given
   * id
   */
  private String getFullName(int id) throws SQLException {
    Statement stmt = this.conn.createStatement();
    String s = "SELECT first_name, middle_name, last_name " +
      "FROM people WHERE id = " + this.id;
    ResultSet rs = stmt.executeQuery(s);
    rs.next();

    StringBuffer fullName = new StringBuffer();

    String firstName = rs.getString("first_name");
    if (firstName != null) {
      fullName.append(firstName);
      fullName.append(' ');
    }

    String middleName = rs.getString("middle_name");
    if (middleName != null) {
      fullName.append(middleName);
      fullName.append(' ');
    }

    String lastName = rs.getString("last_name");
    if (lastName != null) {
      fullName.append(lastName);
      fullName.append(' ');
    }

    return fullName.toString();
  }

  public String getDescription() throws RemoteException {
    try {
      return getFullName(this.id);

//       Statement stmt = this.conn.createStatement();
//       String s = "SELECT * FROM people WHERE id = " + this.id;
//       ResultSet rs = stmt.executeQuery(s);
//       rs.next();

    } catch (SQLException ex) {
      String s = "While getting description of " + this.id;
      throw new RemoteException(s, ex);
    }
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
