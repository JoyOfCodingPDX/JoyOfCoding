#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

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
  private PhoneBillDAO dao;

  @BeforeEach
  public void setUp() throws SQLException {
    // Create an in-memory H2 database
    connection = DriverManager.getConnection("jdbc:h2:mem:${artifactId}_test");

    // Create the phone_bills table
    PhoneBillDAO.createTable(connection);

    // Create the DAO
    dao = new PhoneBillDAO(connection);
  }

  @AfterEach
  public void tearDown() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }

  @Test
  public void canPersistAndFetchPhoneBillByCustomerName() throws SQLException {
    String customerName = "Jane Doe";
    PhoneBill bill = new PhoneBill(customerName);

    // Persist the phone bill using the DAO
    dao.save(bill);

    // Fetch the phone bill by customer name
    PhoneBill fetchedBill = dao.findByCustomer(customerName);

    // Validate that the fetched bill matches the original
    assertThat(fetchedBill, is(notNullValue()));
    assertThat(fetchedBill.getCustomer(), is(equalTo(customerName)));
  }

  @Test
  public void returnsNullWhenPhoneBillNotFound() throws SQLException {
    PhoneBill fetchedBill = dao.findByCustomer("Non-existent Customer");
    assertThat(fetchedBill, is(nullValue()));
  }
}

