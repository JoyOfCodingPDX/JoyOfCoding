package edu.pdx.cs410E.jdbc;

import java.io.*;
import java.sql.*;

/**
 * This program uses JDBC to add a new permit to a table in a
 * relational database
 */
public class AddPermit {
  private static PrintStream err = System.err;
  private static PrintStream out = System.out;

  /** The name of the JDBC driver used to connect to the database */
  private static final String DRIVER_NAME =
    "COM.cloudscape.core.RmiJdbcDriver";

  ///////////////////////  Main Program  ///////////////////////

  public static void main(String[] args) {
    String id = args[0];
    String date = args[1];

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
      // We assume the database has been created
      String url = "jdbc:cloudscape:rmi:University";
      conn = DriverManager.getConnection(url);

    } catch (SQLException ex) {
      err.println("While connecting to database: " + ex);
      System.exit(1);
    }

    try {
      Statement stmt = conn.createStatement();
      String s =
        "INSERT INTO permits VALUES (" + id + ", '" + date + "')";
      stmt.executeUpdate(s);

    } catch (SQLException ex) {
      err.println("While adding permit: " + ex);
      ex.printStackTrace(err);
      System.exit(1);
    }
  }

}
