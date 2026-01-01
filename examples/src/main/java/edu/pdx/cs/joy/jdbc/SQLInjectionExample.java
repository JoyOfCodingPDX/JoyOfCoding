package edu.pdx.cs.joy.jdbc;

import java.math.BigDecimal;
import java.sql.*;

/**
 * Demonstrates the security vulnerability of using Statement versus PreparedStatement
 * for database queries. This example shows how SQL injection attacks work and how
 * PreparedStatement protects against them.
 */
public class SQLInjectionExample {

  /**
   * Simple Employee class to hold employee data.
   */
  static class Employee {
    private final String name;
    private final String email;
    private final BigDecimal salary;
    private final String password;

    public Employee(String name, String email, BigDecimal salary, String password) {
      this.name = name;
      this.email = email;
      this.salary = salary;
      this.password = password;
    }

    public String getName() {
      return name;
    }

    public String getEmail() {
      return email;
    }

    public BigDecimal getSalary() {
      return salary;
    }

    public String getPassword() {
      return password;
    }

    @Override
    public String toString() {
      return "Employee{" +
        "name='" + name + '\'' +
        ", email='" + email + '\'' +
        ", salary=" + salary +
        ", password='" + password + '\'' +
        '}';
    }
  }

  /**
   * Creates the employees table in the database.
   */
  private static void createTable(Connection connection) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.execute(
        "CREATE TABLE employees (" +
        "  id IDENTITY PRIMARY KEY," +
        "  name VARCHAR(255) NOT NULL," +
        "  email VARCHAR(255) NOT NULL," +
        "  salary DECIMAL(10, 2) NOT NULL," +
        "  password VARCHAR(255) NOT NULL" +
        ")"
      );
    }
  }

  /**
   * Inserts an employee into the database.
   */
  private static void insertEmployee(Connection connection, Employee employee) throws SQLException {
    String sql = "INSERT INTO employees (name, email, salary, password) VALUES (?, ?, ?, ?)";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, employee.getName());
      statement.setString(2, employee.getEmail());
      statement.setBigDecimal(3, employee.getSalary());
      statement.setString(4, employee.getPassword());
      statement.executeUpdate();
    }
  }

  /**
   * VULNERABLE: Uses Statement with string concatenation, allowing SQL injection.
   *
   * This method is intentionally vulnerable to demonstrate the security risk.
   * A malicious user can use the username "Dave --'" to comment out the password check,
   * gaining unauthorized access to the data.
   */
  private static Employee getEmployeeDataWithStatement(Connection connection, String name, String password) throws SQLException {
    // SECURITY VULNERABILITY: Building SQL with string concatenation
    String sql = "SELECT name, email, salary, password FROM employees WHERE name = '" + name + "' AND password = '" + password + "'";

    System.out.println("\nExecuting SQL with Statement:");
    System.out.println("  SQL: " + sql);

    try (Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(sql)) {

      if (resultSet.next()) {
        return createEmployee(resultSet);
      }
    }

    return null;
  }

  /**
   * SECURE: Uses PreparedStatement with parameter binding, preventing SQL injection.
   *
   * This method properly uses PreparedStatement, which treats user input as data
   * rather than SQL code, preventing SQL injection attacks.
   */
  private static Employee getEmployeeDataWithPreparedStatement(Connection connection, String name, String password) throws SQLException {
    String sql = "SELECT name, email, salary, password FROM employees WHERE name = ? AND password = ?";

    System.out.println("\nExecuting SQL with PreparedStatement:");
    System.out.println("  SQL: " + sql);
    System.out.println("  Parameter 1 (name): " + name);
    System.out.println("  Parameter 2 (password): " + password);

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, name);
      statement.setString(2, password);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return createEmployee(resultSet);
        }
      }
    }

    return null;
  }

  private static Employee createEmployee(ResultSet resultSet) throws SQLException {
    return new Employee(
      resultSet.getString("name"),
      resultSet.getString("email"),
      resultSet.getBigDecimal("salary"),
      resultSet.getString("password")
    );
  }

  public static void main(String[] args) throws SQLException {
    System.out.println("=== SQL Injection Demonstration ===\n");

    // 1) Create an in-memory H2 database
    Connection connection = H2DatabaseHelper.createInMemoryConnection("sqlInjectionDemo");

    // Create the employees table
    createTable(connection);

    // 2) Persist four employees with unique data
    insertEmployee(connection, new Employee("Dave", "dave@example.com", new BigDecimal("85000.00"), "securePass123"));
    insertEmployee(connection, new Employee("Alice", "alice@example.com", new BigDecimal("90000.00"), "aliceSecret"));
    insertEmployee(connection, new Employee("Bob", "bob@example.com", new BigDecimal("78000.00"), "bobPassword"));
    insertEmployee(connection, new Employee("Carol", "carol@example.com", new BigDecimal("92000.00"), "carolPass456"));

    System.out.println("Created employees table and inserted 4 employees.\n");

    // Malicious input: Using SQL injection to bypass password check
    String maliciousUsername = "Dave --'";
    String incorrectPassword = "wrongPassword";

    System.out.println("Attempting to access Dave's data with:");
    System.out.println("  Username: \"" + maliciousUsername + "\"");
    System.out.println("  Password: \"" + incorrectPassword + "\" (incorrect)");

    // 3) Demonstrate SQL injection vulnerability with Statement
    System.out.println("\n--- Using Statement (VULNERABLE) ---");
    try {
      Employee employee = getEmployeeDataWithStatement(connection, maliciousUsername, incorrectPassword);
      if (employee != null) {
        System.out.println("\n️SQL INJECTION SUCCESSFUL! Unauthorized access granted:");
        System.out.println("  Name: " + employee.getName());
        System.out.println("  Email: " + employee.getEmail());
        System.out.println("  Salary: $" + String.format("%.2f", employee.getSalary()));
        System.out.println("  Password: " + employee.getPassword());
        System.out.println("\nThe SQL comment '--' caused the password check to be ignored!");
      } else {
        System.out.println("\nAccess denied (unexpected)");
      }
    } catch (SQLException e) {
      System.out.println("\nError: " + e.getMessage());
    }

    // 4) Demonstrate protection with PreparedStatement
    System.out.println("\n--- Using PreparedStatement (SECURE) ---");
    try {
      Employee employee = getEmployeeDataWithPreparedStatement(connection, maliciousUsername, incorrectPassword);
      if (employee != null) {
        System.out.println("\nAccess granted (unexpected):");
        System.out.println("  " + employee);
      } else {
        System.out.println("\nAccess denied - No employee found with that name and password combination.");
        System.out.println("PreparedStatement treated 'Dave --'' as a literal username, not SQL code.");
      }
    } catch (SQLException e) {
      System.out.println("\nError: " + e.getMessage());
    }

    // Clean up
    connection.close();

    System.out.println("\n=== Summary ===");
    System.out.println("Statement with string concatenation: VULNERABLE to SQL injection");
    System.out.println("PreparedStatement with parameters: SECURE against SQL injection");
  }
}
