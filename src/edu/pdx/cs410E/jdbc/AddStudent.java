package edu.pdx.cs410E.jdbc;

import java.io.*;
import java.sql.*;

/**
 * This program uses JDBC to add a new student to a table in a
 * relational database
 */
public class AddStudent {
  private static PrintStream err = System.err;
  private static PrintStream out = System.out;

  /** The name of the JDBC driver used to connect to the database */
  private static final String DRIVER_NAME =
    "COM.cloudscape.core.RmiJdbcDriver";

  ///////////////////////  Main Program  ///////////////////////

  public static void main(String[] args) {
    String ssn = null;
    String name = null;
    String permit = null;

    if (args.length > 0) {
      ssn = args[0];
      name = args[1];
      if (args.length >= 3) {
        permit = args[2];
      }
    }

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

      if (ssn == null) {
        // Print all students
        out.println("All students:");

        String s = "SELECT * FROM students";
        ResultSet rs = stmt.executeQuery(s);
        while (rs.next()) {
          out.println("  " + rs.getString("ssn") + " " +
                      rs.getString("name"));
        }

      } else {
        // Add the student to the table
        String s = "INSERT INTO students VALUES ('" + ssn + "', '" +
          name + "'";
        if (permit != null) {
          s += ", " + permit;
        }
        s += ")";
        stmt.executeUpdate(s);
      }

    } catch (SQLException ex) {
      err.println("While adding permit: " + ex);
      ex.printStackTrace(err);
      System.exit(1);
    }
  }

}
