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
   * Drops all of the unversity tables.  Remember that the tables must
   * be dropped in the reverse order from which they were added.
   */
  private static void dropTables(Connection conn) throws SQLException {
    Statement stmt = conn.createStatement();

    String[] names = { "permits", "students", "professors",
                       "locations", "sections", "enrollment" };

    for (int i = names.length - 1; i >= 0; i--) {
      try {
        String s = "DROP TABLE " + names[i];
        stmt.executeUpdate(s);

      } catch (SQLException ex) {
        // Ignore...
      }
    }
  }

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

  /**
   * Creates the students table
   */
  private static void createStudentsTable(Connection conn)
    throws SQLException {

    Statement stmt = conn.createStatement();

    String s =
      "CREATE TABLE students " +
      "  (ssn CHAR(11) CONSTRAINT pk_students PRIMARY KEY, " +
      "   name VARCHAR(30), permit INTEGER, " +
      "   CONSTRAINT fk_permit FOREIGN KEY (permit) " +
      "     REFERENCES permits(number))";
    stmt.executeUpdate(s);
  }  

  /**
   * Creates the professors table
   */
  private static void createProfessorsTable(Connection conn)
    throws SQLException {

    Statement stmt = conn.createStatement();

    String s =
      "CREATE TABLE professors " +
      "  (id INTEGER CONSTRAINT pk_professors PRIMARY KEY, " +
      "   name VARCHAR(30), started DATE)";
    stmt.executeUpdate(s);
  }  

  /**
   * Creates the locations table
   */
  private static void createLocationsTable(Connection conn)
    throws SQLException {

    Statement stmt = conn.createStatement();

    String s =
      "CREATE TABLE locations " +
      "  (id INTEGER CONSTRAINT pk_locations PRIMARY KEY, " +
      "   building VARCHAR(20), room INTEGER)";
    stmt.executeUpdate(s);
  }  

  /**
   * Creates the sections table
   */
  private static void createSectionsTable(Connection conn)
    throws SQLException {

    Statement stmt = conn.createStatement();

    String s =
      "CREATE TABLE sections " +
      "  (crn INTEGER CONSTRAINT pk_sections PRIMARY KEY, " +
      "   professor INTEGER, location INTEGER, start TIME, " +
      "   CONSTRAINT fk_sections FOREIGN KEY (professor) " +
      "     REFERENCES professors(id))";
    stmt.executeUpdate(s);
  }  

  /**
   * Creates the enrollment table
   */
  private static void createEnrollmentTable(Connection conn)
    throws SQLException {

    Statement stmt = conn.createStatement();

    String s =
      "CREATE TABLE enrollment " +
      "  (student CHAR(11), section INTEGER, " +
      "   CONSTRAINT fk_enrollment FOREIGN KEY (student) " +
      "   REFERENCES students(ssn))";
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


    try {
      // Delete any existing university tables
      dropTables(conn);

      // Create the tables
      createPermitsTable(conn);
      createStudentsTable(conn);
      createProfessorsTable(conn);
      createLocationsTable(conn);
      createSectionsTable(conn);
      createEnrollmentTable(conn);

    } catch (SQLException ex) {
      err.println("While creating tables: " + ex);
      ex.printStackTrace(err);
      System.exit(1);
    }

  }

}
