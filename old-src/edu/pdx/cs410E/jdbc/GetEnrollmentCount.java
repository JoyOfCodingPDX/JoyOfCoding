package edu.pdx.cs410E.jdbc;

import java.io.*;
import java.sql.*;

/**
 * This program uses a JDBC {@link PreparedStatement} to compute the
 * number of classes that a each of given set of students is enrolled
 * in.
 */
public class GetEnrollmentCount {
  private static PrintStream err = System.err;
  private static PrintStream out = System.out;

  /** The name of the JDBC driver used to connect to the database */
  private static final String DRIVER_NAME =
    "COM.cloudscape.core.RmiJdbcDriver";

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
      // We assume the database has been created
      String url = "jdbc:cloudscape:rmi:University";
      conn = DriverManager.getConnection(url);

    } catch (SQLException ex) {
      err.println("While connecting to database: " + ex);
      System.exit(1);
    }

    try {
      String s = 
        "SELECT COUNT(section) FROM enrollment WHERE student = ?";
      PreparedStatement ps = conn.prepareStatement(s);

      for (int i = 0; i < args.length; i++) {
        String ssn = args[i];
        ps.setString(1, ssn);
        ResultSet rs = ps.executeQuery();
        rs.next();
        out.println("Student " + ssn + " takes " + rs.getInt(1) + 
                    " classes");
      }

    } catch (SQLException ex) {
      err.println("While adding permit: " + ex);
      ex.printStackTrace(err);
      System.exit(1);
    }
  }

}
