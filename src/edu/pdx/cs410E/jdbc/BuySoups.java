package edu.pdx.cs410E.jdbc;

import java.io.*;
import java.sql.*;

/**
 * This program demonstrates the use of JDBC transactions by modifying
 * multiple rows in the "Soups" table in a single transaction.
 */
public class BuySoups {
  private static PrintStream err = System.err;
  private static PrintStream out = System.out;

  /** The name of the JDBC driver used to connect to the database */
  private static final String DRIVER_NAME =
    "COM.cloudscape.core.RmiJdbcDriver";

  /** The name of the table containing the soups */
  private static final String tableName = "Soups";

  ////////////////////////  Static Methods  ////////////////////////

  public static void main(String[] args) throws SQLException {
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

    // Cans of soup to be purchased
    String[] soups = { "Beef stew", "Beef barley" };

    try {
      // Decrement the quantity of each can of soup.  Perform all of
      // these operations in a single transaction.
      conn.setAutoCommit(false);
    
      Statement stmt;
      for (int i = 0; i < soups.length; i++) {
        String s = "UPDATE " + tableName + 
          " SET quantity = quantity - 1 WHERE name = '" + soups[i] +
          "'";
        stmt = conn.createStatement();
        stmt.executeUpdate(s);
      }

      // All updates were successful, commit
      conn.commit();

    } catch (SQLException ex) {
      ex.printStackTrace(err);
      conn.rollback();
    }

  }
}
