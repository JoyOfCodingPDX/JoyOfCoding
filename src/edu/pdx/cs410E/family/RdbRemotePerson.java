package edu.pdx.cs410E.familyTree;

import edu.pdx.cs410J.familyTree.Person;
import java.rmi.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
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

  public int getGender() throws RemoteException {
    try {
      Statement stmt = this.conn.createStatement();
      String s = "SELECT gender FROM people WHERE id = " + this.id;
      ResultSet rs = stmt.executeQuery(s);
      rs.next();
      return rs.getInt("gender");

    } catch (SQLException ex) {
      String s = "While getting gender for " + this.id;
      throw new RemoteException(s, ex);
    }
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
    try {
      Statement stmt = this.conn.createStatement();
      String s =
        "SELECT middle_name FROM people WHERE id = " + this.id;
      ResultSet rs = stmt.executeQuery(s);
      rs.next();
      return rs.getString(1);

    } catch (SQLException ex) {
      String s = "While getting middle name of " + this.id;
      throw new RemoteException(s, ex);
    }
  }

  public void setMiddleName(String middleName) 
    throws RemoteException {

    try {
      Statement stmt = this.conn.createStatement();
      String s = 
        "UPDATE people SET middle_name = '" + middleName + 
        "' WHERE id = " + this.id;
      stmt.executeUpdate(s);

    } catch (SQLException ex) {
      String s = "While setting middle name of " + this.id + " to \"" +
        middleName + "\": " + ex;
      throw new RemoteException(s, ex);
    }
  }

  public String getLastName() throws RemoteException {
    try {
      Statement stmt = this.conn.createStatement();
      String s =
        "SELECT last_name FROM people WHERE id = " + this.id;
      ResultSet rs = stmt.executeQuery(s);
      rs.next();
      return rs.getString(1);

    } catch (SQLException ex) {
      String s = "While getting last name of " + this.id;
      throw new RemoteException(s, ex);
    }
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
    try {
      Statement stmt = this.conn.createStatement();
      String s = "SELECT father FROM people WHERE id = " + this.id;
      ResultSet rs = stmt.executeQuery(s);
      rs.next();
      return rs.getInt("father");

    } catch (SQLException ex) {
      String s = "While getting father id of " + this.id;
      throw new RemoteException(s, ex);
    }
  }

  public void setFatherId(int fatherId) throws RemoteException {
    try {
      Statement stmt = this.conn.createStatement();
      String q = "SELECT id, gender FROM people WHERE id = " +
        fatherId;
      ResultSet rs = stmt.executeQuery(q);
      if (!rs.next()) {
        String s = "Could not find person with id " + fatherId;
        throw new IllegalArgumentException(s);

      } else if (rs.getInt("gender") != Person.MALE) {
        String s = "A father cannot be FEMALE";
        throw new IllegalArgumentException(s);
      }

    } catch (SQLException ex) {
      String s = "While examining father with id " + fatherId;
      throw new RemoteException(s, ex);
    }

    try {
      Statement stmt = this.conn.createStatement();
      String s = "UPDATE people SET father = " + fatherId +
        " WHERE id = " + this.id;
      stmt.executeUpdate(s);

    } catch (SQLException ex) {
      String s = "While setting father id of " + this.id + " to " +
        fatherId;
      throw new RemoteException(s, ex);
    }
  }

  public int getMotherId() throws RemoteException {
    try {
      Statement stmt = this.conn.createStatement();
      String s = "SELECT mother FROM people WHERE id = " + this.id;
      ResultSet rs = stmt.executeQuery(s);
      rs.next();
      return rs.getInt("mother");

    } catch (SQLException ex) {
      String s = "While getting mother id of " + this.id;
      throw new RemoteException(s, ex);
    }
  }

  public void setMotherId(int motherId) throws RemoteException {
    try {
      Statement stmt = this.conn.createStatement();
      String q = "SELECT id, gender FROM people WHERE id = " +
        motherId;
      ResultSet rs = stmt.executeQuery(q);
      if (!rs.next()) {
        String s = "Could not find person with id " + motherId;
        throw new IllegalArgumentException(s);

      } else if (rs.getInt("gender") != Person.FEMALE) {
        String s = "A mother cannot be MALE";
        throw new IllegalArgumentException(s);
      }

    } catch (SQLException ex) {
      String s = "While examining mother with id " + motherId;
      throw new RemoteException(s, ex);
    }

    try {
      Statement stmt = this.conn.createStatement();
      String s = "UPDATE people SET mother = " + motherId +
        " WHERE id = " + this.id;
      stmt.executeUpdate(s);

    } catch (SQLException ex) {
      String s = "While setting mother id of " + this.id + " to " +
        motherId;
      throw new RemoteException(s, ex);
    }
  }

  public Date getDateOfBirth() throws RemoteException {
    try {
      Statement stmt = this.conn.createStatement();
      String s = "SELECT dob FROM people WHERE id = " + this.id;
      ResultSet rs = stmt.executeQuery(s);
      rs.next();
      return new Date(rs.getDate("dob").getTime());

    } catch (SQLException ex) {
      String s = "While getting date of birth of " + this.id;
      throw new RemoteException(s, ex);
    }
  }

  public void setDateOfBirth(Date dob) throws RemoteException {
    try {
      Statement stmt = this.conn.createStatement();
      String s = "UPDATE people SET dob = '" + 
        (new java.sql.Date(dob.getTime())) + "' WHERE id = " +
        this.id;
      stmt.executeUpdate(s);

    } catch (SQLException ex) {
      String s = "While setting date of birth of " + this.id;
      throw new RemoteException(s, ex);
    }
  }

  public Date getDateOfDeath() throws RemoteException {
    try {
      Statement stmt = this.conn.createStatement();
      String s = "SELECT dod FROM people WHERE id = " + this.id;
      ResultSet rs = stmt.executeQuery(s);
      rs.next();
      return new Date(rs.getDate("dod").getTime());

    } catch (SQLException ex) {
      String s = "While getting date of death of " + this.id;
      throw new RemoteException(s, ex);
    }
  }

  public void setDateOfDeath(Date dod) throws RemoteException {
    try {
      Statement stmt = this.conn.createStatement();
      String s = "UPDATE people SET dod = '" + 
        (new java.sql.Date(dod.getTime())) + "' WHERE id = " +
        this.id;
      stmt.executeUpdate(s);

    } catch (SQLException ex) {
      String s = "While setting date of death of " + this.id;
      throw new RemoteException(s, ex);
    }
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
    DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);

    try {
      StringBuffer sb = new StringBuffer("Person ");
      sb.append(this.id);
      sb.append(": ");
      sb.append(getFullName(this.id));

      Statement stmt = this.conn.createStatement();
      String s = "SELECT dob, dod, mother, father " +
        "FROM people WHERE id = " + this.id;
      ResultSet rs = stmt.executeQuery(s);
      rs.next();
      Date dob = rs.getDate("dob");
      if (dob != null) {
        sb.append("\nBorn: " + df.format(dob));
      }
      Date dod = rs.getDate("dod");
      if (dod != null) {
        sb.append("\nDied: " + df.format(dod));
      }

      int motherId = rs.getInt("mother");
      if (!rs.wasNull()) {
        sb.append("\nMother: ");
        sb.append(getFullName(motherId));
      }

      int fatherId = rs.getInt("father");
      if (!rs.wasNull()) {
        sb.append("\nFather: ");
        sb.append(getFullName(fatherId));
      }

      return sb.toString();

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
