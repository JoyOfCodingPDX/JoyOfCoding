package edu.pdx.cs.joy.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * A command-line program that demonstrates persisting a Department object
 * to an H2 database file using the DepartmentDAO class.
 */
public class PersistDepartment {

  /**
   * Main method that persists a new Department to an H2 database file.
   *
   * @param args command line arguments where args[0] is the path to the database file
   *             and args[1] is the name of the department to create
   * @throws SQLException if a database error occurs
   */
  public static void main(String[] args) throws SQLException {
    if (args.length < 2) {
      System.err.println("Missing required arguments");
      System.err.println("Usage: java PersistDepartment <database-file-path> <department-name>");
      System.exit(1);
    }

    String dbFilePath = args[0];
    String departmentName = args[1];

    File dbFile = new File(dbFilePath);

    try (Connection connection = H2DatabaseHelper.createFileBasedConnection(dbFile)) {
      // Create the departments table
      DepartmentDAO.createTable(connection);

      // Create a new DepartmentDAO
      DepartmentDAO departmentDAO = new DepartmentDAO(connection);

      // Create a new Department object with the name from the command line
      Department department = new Department(departmentName);

      // Persist the department to the database
      departmentDAO.save(department);

      // Print information about the persisted department
      System.out.println("Successfully persisted department to database at: " + dbFile.getAbsolutePath());
      System.out.println(department);
      System.out.println("Auto-generated ID: " + department.getId());
    }
  }
}
