package edu.pdx.cs410E.jdbc;

import java.io.*;
import java.sql.*;

/**
 * This program uses JDBC to create a Cloudscape database named
 * "University" and add the appropriate tables to it.
 */
public class CreateUniversity {
  private static PrintStream err = System.err;
  private static PrintStream out = System.out;

  /** The name of the JDBC driver used to connect to the database */
  private static final String DRIVER_NAME =
    "COM.cloudscape.core.RmiJdbcDriver";

  //////////////////////  Static Methods  //////////////////////

  /**
   * Creates the permits table
   */
  private static void createPermitsTable(Connection conn)
    throws SQLException {

    Statement stmt = conn.createStatement();
    String s =
      "CREATE TABLE permits" +
      "  (number INTEGER CONSTRAINT pk_permits PRIMARY KEY," +
      "   expiration DATE)";
    stmt.executeUpdate(s);
  }

  ///////////////////////  Main Program  ///////////////////////

  public static void main(String[] args) {
    // Install the Cloudscape Driver
    try {
      // Initialize the database driver
      Class.forName(DRIVER_NAME).newInstance();

    } catch (Exception ex) {
      err.println("Could not load driver: " + DRIVER_NAME);
      System.exit(1);
    }

    // Get a connection to Cloudscape, create the "University"
    // database if necessary
    Connection conn = null;
    try {
      String url = "jdbc:cloudscape:rmi:University;create=true";
      conn = DriverManager.getConnection(url);

    } catch (SQLException ex) {
      err.println("While connecting to database: " + ex);
      System.exit(1);
    }

    // Create the tables
    try {
      createPermitsTable(conn);

    } catch (SQLException ex) {
      err.println("While creating tables: " + ex);
      System.exit(1);
    }

  }

}
