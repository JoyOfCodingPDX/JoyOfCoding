package edu.pdx.cs410E.family;

import edu.pdx.cs399J.family.*;
import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.server.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * This <code>RemoteFamilyTree</code> is backed by tables stored in a
 * relational database.  When the remote client invokes methods that
 * change the family tree, the underlying tables rows are updated.
 */
class RdbRemoteFamilyTree extends UnicastRemoteObject
  implements RemoteFamilyTree {

  /** The name of the JDBC driver used to connect to the database */
  static final String DRIVER_NAME =
    "COM.cloudscape.core.RmiJdbcDriver";

  //////////////////////  Instance Fields  //////////////////////

  /** A connection to the relational database */
  private transient Connection conn;

  /** The highest id among the people in this family tree */
  private transient int highestId;

  /** Maps ids to their RdbRemotePersons */
  private transient Map remotePersons = new HashMap();

  ////////////////////////  Constructors  ///////////////////////

  /**
   * Creates a new <code>RdbRemoteFamilyTree</code> and makes a
   * connection to the database of the given name (usually the family
   * name).
   */
  RdbRemoteFamilyTree(String dbName) throws RemoteException {
    super();    // Registers this object with RMI runtime

    // Install the database Driver
    try {
      // Initialize the database driver
      Class.forName(DRIVER_NAME).newInstance();

    } catch (Exception ex) {
      String s = "Could not load driver: " + DRIVER_NAME;
      throw new RemoteException(s, ex);
    }

    // Get a connection to the database, we assume that the database
    // already exists
    try {
      String url = "jdbc:cloudscape:rmi:" + dbName;
      this.conn = DriverManager.getConnection(url);

    } catch (SQLException ex) {
      String s = "While connecting to database " + dbName;
      throw new RemoteException(s, ex);
    }

    // Do a database query to determine what the highest id among all
    // of the people is
    try {
      Statement stmt = this.conn.createStatement();
      String s = "SELECT MAX(id) FROM people";
      ResultSet rs = stmt.executeQuery(s);
      
      if (rs.next()) {
        this.highestId = rs.getInt(1);

      } else {
        this.highestId = 0;
      }

    } catch (SQLException ex) {
      throw new RemoteException("While computing highest id", ex);
    }

  }

  ////////////////////  Instance Methods  ////////////////////

  public RemotePerson createPerson(int gender)
    throws RemoteException {

    if (gender != Person.MALE && gender != Person.FEMALE) {
      String s = "Invalid gender: " + gender;
      throw new IllegalArgumentException(s);
    }

    // Add a new row to the people table and create a new
    // RdbRemotePerson 
    int id = ++this.highestId;
    try {
      Statement stmt = this.conn.createStatement();
      String s = "INSERT INTO people (id, gender) " +
        "VALUES (" + id + ", " + gender + ")";
      stmt.executeUpdate(s);

    } catch (SQLException ex) {
      throw new RemoteException("While creating person", ex);
    }

    return getPerson(id);
  }

  public RemotePerson getPerson(int id) throws RemoteException {
    RemotePerson rPerson = 
      (RemotePerson) this.remotePersons.get(new Integer(id));
    if (rPerson == null) {
      // Is there a person in the database with that id?
      try {
        Statement stmt = this.conn.createStatement();
        String s = "SELECT id FROM people WHERE id = " + id;
        ResultSet rs = stmt.executeQuery(s);
        if (!rs.next()) {
          return null;

        } else {
          rPerson = new RdbRemotePerson(id, this.conn);
          this.remotePersons.put(new Integer(id), rPerson);
        }

      } catch (SQLException ex) {
        throw new RemoteException("While getting person " + id, ex);
      }
    }

    return rPerson;
  }

  public RemotePerson getPerson(String firstName, String lastName)
    throws RemoteException {

    try {
      Statement stmt = this.conn.createStatement();
      String s = "SELECT id FROM people WHERE first_name = '" +
        firstName + "' AND last_name = '" + lastName + "'";
      ResultSet rs = stmt.executeQuery(s);

      if (rs.next()) {
        int id = rs.getInt(1);
        if (rs.next()) {
          String m = "Multiple people named \"" + firstName + 
            " " + lastName + "\" " + id; // + " and " + rs.getInt(1);
          throw new IllegalArgumentException(m);
        }

        return getPerson(id);

      } else {
        return null;
      }

    } catch (SQLException ex) {
      String s = "While getting \"" + firstName + " " + lastName +
        "\"";
      throw new RemoteException(s, ex);
    }

  }

  public void shutdown() throws IOException, RemoteException {
    // Close the connection to the relational database
    try {
      this.conn.close();

    } catch (SQLException ex) {
      String s = "While closing connection";
      throw new RemoteException(s, ex);
    }
  }

  public RemoteMarriage getMarriage(int husbandId, int wifeId) 
    throws RemoteException {

    throw new UnsupportedOperationException("getMarriage() Not implemented yet");
  }

  public RemoteMarriage createMarriage(int husbandId, int wifeId) 
    throws RemoteException {
    
    RemotePerson husband = getPerson(husbandId);
    if (husband == null) {
      String s = "Husband " + husbandId + " does not exist";
      throw new IllegalArgumentException(s);

    } else if (husband.getGender() != Person.MALE) {
      String s = "Husband " + husbandId + " must be MALE";
      throw new IllegalArgumentException(s);
    }

    RemotePerson wife = getPerson(wifeId);
    if (wife == null) {
      String s = "Wife " + wifeId + " does not exist";
      throw new IllegalArgumentException(s);

    } else if (husband.getGender() != Person.FEMALE) {
      String s = "Wife " + wifeId + " must be FEMALE";
      throw new IllegalArgumentException(s);
    }

    // Everybody is okay, make an entry in the table
    try {
      Statement stmt = this.conn.createStatement();
      String s = "INSERT INTO marriages (husband, wife) " +
        "VALUES (" + husbandId + ", " + wifeId + ")";
      stmt.executeUpdate(s);

    } catch (SQLException ex) {
      String s = "While creating a marriage between " + husbandId + 
        " and " + wifeId;
      throw new RemoteException(s, ex);
    }

    return new RdbRemoteMarriage(husbandId, wifeId, this.conn);
  }

  public Collection getLiving() throws RemoteException {
    throw new UnsupportedOperationException("getLiving() Not implemented yet");
  }

  public Collection getLiving(Date date) throws RemoteException {
    throw new UnsupportedOperationException("getLiving(Date) Not implemented yet");
  }

  //////////////////////////  Main Program  //////////////////////////

  private static PrintStream out = System.out;
  private static PrintStream err = System.err;

  /**
   * Prints usage information about this program
   */
  private static void usage(String s) {
    err.println("\n** " + s + "\n");
    err.println("usage: java RdbRemoteFamilyTree [-start " +
                "| -stop] familyName host port");
    err.println("");
    err.println("This program creates a new RdbRemoteFamilyTree and " +
                "binds it into the RMI registry.  It is assumed " +
                "that the tables containing the family tree data " +
                "have already been created.");
    System.exit(1);
  }

  public static void main(String[] args) {
    String familyName = null;
    String host = null;
    int port = -1;
    String command = null;

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-start")) {
        command = "START";

      } else if (args[i].equals("-stop")) {
        command = "STOP";

      } else if (familyName == null) {
        familyName = args[i];

      } else if (host == null) {
        host = args[i];

      } else if (port == -1) {
        try {
          port = Integer.parseInt(args[i]);

        } catch (NumberFormatException ex) {
          usage("Invalid port: " + args[i]);
        }

      } else {
        usage("Spurious command line: " + args[i]);
      }
    }

    if (command == null) {
      usage("Missing command");
    }

    if (familyName == null) {
      usage("Missing family name");
    }

    if (host == null) {
      usage("Missing host name");
    }

    if (port == -1) {
      usage("Missing port number");
    }

    if (command.equals("START")) {
      // Install an RMISecurityManager, if there is not a
      // SecurityManager already installed
      if (System.getSecurityManager() == null) {
        System.setSecurityManager(new RMISecurityManager());
      }

      String name = "rmi://" + host + ":" + port + "/" + familyName;

      try {
        RdbRemoteFamilyTree tree =
          new RdbRemoteFamilyTree(familyName);
        Naming.rebind(name, tree);
        out.println("Successfully bound RdbRemoteFamilyTree");

      } catch (Exception ex) {
        ex.printStackTrace(System.err);
      }


    } else if (command.equals("STOP")) {
      // Install an RMISecurityManager, if there is not a
      // SecurityManager already installed
      if (System.getSecurityManager() == null) {
        System.setSecurityManager(new RMISecurityManager());
      }

      String name = "rmi://" + host + ":" + port + "/" + familyName;

      try {
        RemoteFamilyTree tree = 
          (RemoteFamilyTree) Naming.lookup(name);
        tree.shutdown();
        Naming.unbind(name);

      } catch (Exception ex) {
        ex.printStackTrace(err);
      }
      

    } else {
      String s = "Unknown command: " + command;
      throw new IllegalStateException(s);
    }
  }

}
