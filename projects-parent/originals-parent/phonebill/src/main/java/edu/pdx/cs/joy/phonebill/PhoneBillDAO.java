package edu.pdx.cs.joy.phonebill;

import java.sql.*;

/**
 * A Data Access Object (DAO) for persisting PhoneBill instances to a database.
 *
 * This is a simple example to demonstrate basic JDBC operations.
 * Students can expand this to include PhoneCall persistence and more
 * sophisticated query capabilities.
 */
public class PhoneBillDAO {

  private final Connection connection;

  /**
   * Creates a new PhoneBillDAO with the specified database connection.
   *
   * @param connection the database connection to use
   */
  public PhoneBillDAO(Connection connection) {
    this.connection = connection;
  }

  /**
   * Creates the customers table in the database.
   *
   * @param connection the database connection to use
   * @throws SQLException if a database error occurs
   */
  public static void createTable(Connection connection) throws SQLException {
    String createTableSQL =
      "CREATE TABLE customers (" +
      "  name VARCHAR(255) PRIMARY KEY" +
      ")";

    try (Statement statement = connection.createStatement()) {
      statement.execute(createTableSQL);
    }
  }

  /**
   * Saves a PhoneBill to the database.
   *
   * @param bill the phone bill to save
   * @throws SQLException if a database error occurs
   */
  public void save(PhoneBill bill) throws SQLException {
    String insertSQL = "INSERT INTO customers (name) VALUES (?)";

    try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
      statement.setString(1, bill.getCustomer());
      statement.executeUpdate();
    }
  }

  /**
   * Finds a PhoneBill by customer name.
   *
   * @param customerName the customer name to search for
   * @return the PhoneBill for the customer, or null if not found
   * @throws SQLException if a database error occurs
   */
  public PhoneBill findByCustomer(String customerName) throws SQLException {
    String selectSQL = "SELECT name FROM customers WHERE name = ?";

    try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {
      statement.setString(1, customerName);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          String name = resultSet.getString("name");
          return new PhoneBill(name);
        }
      }
    }

    return null;
  }
}

