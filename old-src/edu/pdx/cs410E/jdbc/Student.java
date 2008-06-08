package edu.pdx.cs410E.jdbc;

import java.io.*;
import java.sql.*;

/**
 * This class models a student whose data is stored in a relational
 * database.
 */
public class Student {
  private static PrintStream err = System.err;
  private static PrintStream out = System.out;

  /** The name of the JDBC driver used to connect to the database */
  private static final String DRIVER_NAME =
    "COM.cloudscape.core.RmiJdbcDriver";

  //////////////////////  Instance Fields  //////////////////////

  /** The student's social security number */
  private String ssn;

  /** The student's name */
  private String name;

  /** The id of a student's parking permit.  A value of 0 indicates
   * that the student does not have a permit. */
  private int permit;

  ////////////////////////  Constructors  ////////////////////////

  /**
   * Creates a new student with the given social security number.  If
   * a student with the given SSN cannot be found in the database, a
   * new row will be added.
   */
  public Student(String ssn) {
    Connection conn = getConnection();
    
    
  }

  //////////////////////  Instance Methods  //////////////////////

  /**
   * Helper methods that obtains a connection to the database
   */
  private Connection getConnection() {
    try {
      // Initialize the database driver
      Class.forName(DRIVER_NAME).newInstance();

    } catch (Exception ex) {
      err.println("Could not load driver: " + DRIVER_NAME);
      System.exit(1);
    }

    try {
      // Get a connection to the desired database
      String url = "jdbc:cloudscape:rmi:University";
      return DriverManager.getConnection(url);

    } catch (SQLException ex) {
      err.println("While connecting to database: " + ex);
      System.exit(1);
    }

    // Keep compiler happy
    return null;
  }

  public String toString() {
    return "Student " + this.ssn + " " + this.name + 
      (this.permit != 0 ? " " + this.permit : "");
  }

  ///////////////////////  Main Program  ///////////////////////

  /**
   * Main program that accesses the database and creates a new
   * <code>Student</code> then prints it out
   */
  public static void main(String[] args) {
    String ssn = args[0];
    out.println(new Student(ssn));
  }

}
