package edu.pdx.cs410E.familyTree;

import edu.pdx.cs410J.familyTree.Person;
import java.io.*;
import java.sql.*;

/**
 * This program creates tables in a relational database that store the
 * data for the family tree application.
 */
public class CreateFamilyTreeTables {
  private static PrintStream out = System.out;
  private static PrintStream err = System.err;

  //////////////////////  Static Methods  //////////////////////

  /**
   * Helper method that creates the family tree tables.  If the tables
   * already exist, they are dropped.
   */
  static void createTables(Connection conn) throws SQLException {
    // Drop the people table
    try {
      Statement stmt = conn.createStatement();
      String s = "DROP TABLE people";
      stmt.executeUpdate(s);

    } catch (SQLException ex) {
      System.out.println("Ignoring: " + ex);
    }

    // Create the people table
    Statement stmt = conn.createStatement();

    String s = "CREATE TABLE people" +
      " (id INTEGER CONSTRAINT pk_people PRIMARY KEY, " +
      "  gender INTEGER CONSTRAINT valid_gender CHECK (gender IN(" +
      Person.MALE + ", " + Person.FEMALE + "))," +
      "  first_name VARCHAR(20), middle_name VARCHAR(20), " +
      "  last_name VARCHAR(20), " +
      "  father INTEGER CONSTRAINT fk_father REFERENCES people (id), " +
      "  mother INTEGER CONSTRAINT fk_mother REFERENCES people (id), " +
      "  dob DATE, dod DATE)";
    stmt.executeUpdate(s);
  }

  ///////////////////////  Main Program  ///////////////////////

  private static void usage(String s) {
    err.println("\n** " + s + "\n");
    err.println("usage: java CreateFamilyTreeTables familyName");
    err.println("  familyName   Name of the database");
    err.println("");
    err.println("This program creates the tables in a relational " +
                "database that store the");
    err.println("data for the family tree application");
    err.println("");
    System.exit(1);
  }

  public static void main(String[] args) {
    String familyName = null;

    for (int i = 0; i < args.length; i++) {
      if (familyName == null) {
        familyName = args[i];

      } else {
        usage("Spurious commane line: " + args[i]);
      }
    }

    if (familyName == null) {
      usage("Missing family name");
    }

    // Install the database Driver
    try {
      // Initialize the database driver
      Class.forName(RdbRemoteFamilyTree.DRIVER_NAME).newInstance();

    } catch (Exception ex) {
      err.println("Could not load driver: " +
                  RdbRemoteFamilyTree.DRIVER_NAME + ": " + ex);
      System.exit(1);
    }

    // Get a connection to the database, we assume that the database
    // already exists
    Connection conn = null;
    try {
      String url = "jdbc:cloudscape:rmi:" + familyName +
        ";create=true";
      conn = DriverManager.getConnection(url);

    } catch (SQLException ex) {
      err.println("While connecting to database " + familyName + 
                  ": " + ex);
      System.exit(1);
    }

    try {
      createTables(conn);

    } catch (SQLException ex) {
      err.println("While creating tables: " + ex);
      System.exit(1);
    }
  }

}
