package edu.pdx.cs410E.jdbc;

import java.io.*;
import java.sql.*;

/**
 * This program creates a Cloudscape database named "Soups" and adds
 * some data to it.
 */
public class SetupSoups {
  private static PrintStream err = System.err;
  private static PrintStream out = System.out;

  /** The name of the JDBC driver used to connect to the database */
  private static final String DRIVER_NAME =
    "COM.cloudscape.core.RmiJdbcDriver";

  /** The name of the table containing the soups */
  private static final String tableName = "Soups";

  ////////////////////  Static Methods  ////////////////////

  private static void addSoup(Connection conn, String name, 
                              int quantity) throws SQLException {
    Statement stmt = conn.createStatement();
    String s = "INSERT INTO " + tableName + " VALUES (\'" + name + 
      "\', " + quantity + ")";
    stmt.executeUpdate(s);
  }

  public static void main(String[] args) {
    // Install the Cloudscape Driver
    try {
      // Initialize the database driver
      Class.forName(DRIVER_NAME).newInstance();

    } catch (Exception ex) {
      err.println("Could not load driver: " + DRIVER_NAME);
      System.exit(1);
    }

    // Get a connection to Cloudscape, create the "Soups"
    // database if necessary
    Connection conn = null;
    try {
      String url = "jdbc:cloudscape:rmi:Soups;create=true";
      conn = DriverManager.getConnection(url);

    } catch (SQLException ex) {
      err.println("While connecting to database: " + ex);
      System.exit(1);
    }

    try {
      try {
        // Drop the table if it exists
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("DROP TABLE " + tableName);

      } catch (SQLException ex) {
        // ignore
      }

      // Create the table
      Statement stmt = conn.createStatement();
      String s = "CREATE TABLE " + tableName +
        " (name VARCHAR(30), quantity INTEGER " +
        "CHECK (quantity >= 0))";
      stmt.executeUpdate(s);

      // Populate the table
      addSoup(conn, "Chicken noodle", 4);
      addSoup(conn, "Beef stew", 3);
      addSoup(conn, "Tomato", 2);
      addSoup(conn, "Beef barley", 2);

    } catch (SQLException ex) {
      ex.printStackTrace(err);
      System.exit(1);
    }

  }

}
