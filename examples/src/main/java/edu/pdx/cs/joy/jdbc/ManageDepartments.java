package edu.pdx.cs.joy.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * A command-line program that demonstrates CRUD operations on Department objects
 * using the DepartmentDAO class with an H2 database.
 */
public class ManageDepartments {

  /**
   * Main method that performs CRUD operations on departments in an H2 database file.
   *
   * @param args command line arguments where args[0] is the path to the database file
   *             and args[1] is the command (create, retrieve, update, delete, or list)
   * @throws SQLException if a database error occurs
   */
  public static void main(String[] args) throws SQLException {
    if (args.length < 2) {
      printUsage();
      return;
    }

    String dbFilePath = args[0];
    String command = args[1].toLowerCase();

    File dbFile = new File(dbFilePath);

    try (Connection connection = H2DatabaseHelper.createFileBasedConnection(dbFile)) {
      // Create the departments table if it doesn't exist
      DepartmentDAO.createTable(connection);

      // Create a new DepartmentDAO
      DepartmentDAO departmentDAO = new DepartmentDAO(connection);

      switch (command) {
        case "create":
          handleCreate(args, departmentDAO);
          break;
        case "retrieve":
          handleRetrieve(args, departmentDAO);
          break;
        case "update":
          handleUpdate(args, departmentDAO);
          break;
        case "delete":
          handleDelete(args, departmentDAO);
          break;
        case "list":
          handleList(departmentDAO);
          break;
        default:
          System.err.println("Unknown command: " + command);
          printUsage();
          return;
      }
    }
  }

  private static void printUsage() {
    System.err.println("Usage: java ManageDepartments <database-file-path> <command> [args...]");
    System.err.println();
    System.err.println("Commands:");
    System.err.println("  create <name>           - Create a new department with the given name");
    System.err.println("  retrieve <id>           - Retrieve a department by ID");
    System.err.println("  update <id> <name>      - Update the name of a department");
    System.err.println("  delete <id>             - Delete a department by ID");
    System.err.println("  list                    - List all departments");
  }

  private static void handleCreate(String[] args, DepartmentDAO departmentDAO) throws SQLException {
    if (args.length < 3) {
      System.err.println("Missing department name for create command");
      System.err.println("Usage: java ManageDepartments <database-file-path> create <name>");
      return;
    }

    String departmentName = args[2];
    Department department = new Department(departmentName);
    departmentDAO.save(department);

    System.out.println("Successfully created department:");
    System.out.println(department);
    System.out.println("Auto-generated ID: " + department.getId());
  }

  private static void handleRetrieve(String[] args, DepartmentDAO departmentDAO) throws SQLException {
    if (args.length < 3) {
      System.err.println("Missing department ID for retrieve command");
      System.err.println("Usage: java ManageDepartments <database-file-path> retrieve <id>");
      return;
    }

    int id = Integer.parseInt(args[2]);
    Department department = departmentDAO.findById(id);

    if (department == null) {
      System.out.println("No department found with ID: " + id);
    } else {
      System.out.println("Found department:");
      System.out.println(department);
    }
  }

  private static void handleUpdate(String[] args, DepartmentDAO departmentDAO) throws SQLException {
    if (args.length < 4) {
      System.err.println("Missing arguments for update command");
      System.err.println("Usage: java ManageDepartments <database-file-path> update <id> <name>");
      return;
    }

    int id = Integer.parseInt(args[2]);
    String newName = args[3];

    Department department = departmentDAO.findById(id);
    if (department == null) {
      System.out.println("No department found with ID: " + id);
      return;
    }

    department.setName(newName);
    departmentDAO.update(department);

    System.out.println("Successfully updated department:");
    System.out.println(department);
  }

  private static void handleDelete(String[] args, DepartmentDAO departmentDAO) throws SQLException {
    if (args.length < 3) {
      System.err.println("Missing department ID for delete command");
      System.err.println("Usage: java ManageDepartments <database-file-path> delete <id>");
      return;
    }

    int id = Integer.parseInt(args[2]);
    Department department = departmentDAO.findById(id);

    if (department == null) {
      System.out.println("No department found with ID: " + id);
      return;
    }

    departmentDAO.delete(id);
    System.out.println("Successfully deleted department:");
    System.out.println(department);
  }

  private static void handleList(DepartmentDAO departmentDAO) throws SQLException {
    List<Department> allDepartments = departmentDAO.findAll();
    System.out.println("Found " + allDepartments.size() + " department(s)");
    for (Department dept : allDepartments) {
      System.out.println("  " + dept);
    }
  }
}
