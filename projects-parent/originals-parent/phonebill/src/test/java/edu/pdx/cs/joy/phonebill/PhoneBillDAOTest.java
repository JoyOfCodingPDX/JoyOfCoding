package edu.pdx.cs.joy.phonebill;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * A simple example unit test that demonstrates persisting a PhoneBill
 * to an H2 in-memory database using JDBC.
 *
 * This is a starting point for students to understand how to use
 * Data Access Objects (DAOs) to persist domain objects to a database.
 */
public class PhoneBillDAOTest {

  private Connection connection;

  @BeforeEach
  public void setUp() throws SQLException {
    // Create an in-memory H2 database
    connection = DriverManager.getConnection("jdbc:h2:mem:phonebill_test");

    // Create the phone_bills table
    createTable();
  }

  @AfterEach
  public void tearDown() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }

  /**
   * Creates the phone_bills table in the database.
   */
  private void createTable() throws SQLException {
    String createTableSQL =
      "CREATE TABLE phone_bills (" +
      "  customer_name VARCHAR(255) PRIMARY KEY" +
      ")";

    try (Statement statement = connection.createStatement()) {
      statement.execute(createTableSQL);
    }
  }

  @Test
  public void canPersistAndFetchPhoneBillByCustomerName() throws SQLException {
    String customerName = "Jane Doe";
    PhoneBill bill = new PhoneBill(customerName);

    // Persist the phone bill
    savePhoneBill(bill);

    // Fetch the phone bill by customer name
    PhoneBill fetchedBill = findPhoneBillByCustomer(customerName);

    // Validate that the fetched bill matches the original
    assertThat(fetchedBill, is(notNullValue()));
    assertThat(fetchedBill.getCustomer(), is(equalTo(customerName)));
  }

  @Test
  public void returnsNullWhenPhoneBillNotFound() throws SQLException {
    PhoneBill fetchedBill = findPhoneBillByCustomer("Non-existent Customer");
    assertThat(fetchedBill, is(nullValue()));
  }

  /**
   * Saves a PhoneBill to the database.
   *
   * @param bill the phone bill to save
   * @throws SQLException if a database error occurs
   */
  private void savePhoneBill(PhoneBill bill) throws SQLException {
    String insertSQL = "INSERT INTO phone_bills (customer_name) VALUES (?)";

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
  private PhoneBill findPhoneBillByCustomer(String customerName) throws SQLException {
    String selectSQL = "SELECT customer_name FROM phone_bills WHERE customer_name = ?";

    try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {
      statement.setString(1, customerName);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          String name = resultSet.getString("customer_name");
          return new PhoneBill(name);
        }
      }
    }

    return null;
  }
}

