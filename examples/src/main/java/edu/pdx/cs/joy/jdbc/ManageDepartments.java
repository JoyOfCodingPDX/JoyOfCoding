package edu.pdx.cs.joy.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * A command-line program that demonstrates persisting a Department object
 * to an H2 database file using the DepartmentDAO class.
 */
public class ManageDepartments {

  /**
   * Main method that persists a new Department to an H2 database file.
   *
   * @param args command line arguments where args[0] is the path to the database file
   *             and args[1] is the optional name of the department to create
   * @throws SQLException if a database error occurs
   */
  public static void main(String[] args) throws SQLException {
    if (args.length < 1) {
      System.err.println("Missing database file path");
      System.err.println("Usage: java ManageDepartments <database-file-path> [department-name");
      System.exit(1);
    }

    String dbFilePath = args[0];
    String departmentName = (args.length > 1 ? args[1] : null);

    File dbFile = new File(dbFilePath);

    try (Connection connection = H2DatabaseHelper.createFileBasedConnection(dbFile)) {
      // Create the departments table
      DepartmentDAO.createTable(connection);

      // Create a new DepartmentDAO
      DepartmentDAO departmentDAO = new DepartmentDAO(connection);

      if (departmentName == null) {
        List<Department> allDepartments = departmentDAO.findAll();
        System.out.println("Found " + allDepartments.size() + " departments");
        for (Department dept : allDepartments) {
          System.out.println("  " + dept);
        }

      } else {
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
}
