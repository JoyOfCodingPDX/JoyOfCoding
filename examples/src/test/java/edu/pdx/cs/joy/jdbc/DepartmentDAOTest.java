package edu.pdx.cs.joy.jdbc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DepartmentDAOTest {

  private Connection connection;
  private DepartmentDAO departmentDAO;

  @BeforeEach
  public void setUp() throws SQLException {
    // Create an in-memory H2 database
    connection = H2DatabaseHelper.createInMemoryConnection("test");

    // Drop the table if it exists from a previous test, then create it
    DepartmentDAO.dropTable(connection);
    DepartmentDAO.createTable(connection);

    // Initialize the DAO with the connection
    departmentDAO = new DepartmentDAO(connection);
  }

  @AfterEach
  public void tearDown() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      // Drop the table and close the connection
      DepartmentDAO.dropTable(connection);
      connection.close();
    }
  }

  @Test
  public void testPersistAndFetchDepartmentById() throws SQLException {
    // Create a department
    Department department = new Department(101, "Computer Science");

    // Persist the department
    departmentDAO.save(department);

    // Fetch the department by ID
    Department fetchedDepartment = departmentDAO.findById(101);

    // Validate the fetched department using Hamcrest assertions
    assertThat(fetchedDepartment, is(notNullValue()));
    assertThat(fetchedDepartment.getId(), is(equalTo(101)));
    assertThat(fetchedDepartment.getName(), is(equalTo("Computer Science")));
  }

  @Test
  public void testFindDepartmentByName() throws SQLException {
    // Create and persist a department
    Department department = new Department(102, "Mathematics");
    departmentDAO.save(department);

    // Fetch the department by name
    Department fetchedDepartment = departmentDAO.findByName("Mathematics");

    // Validate the fetched department
    assertThat(fetchedDepartment, is(notNullValue()));
    assertThat(fetchedDepartment.getId(), is(equalTo(102)));
    assertThat(fetchedDepartment.getName(), is(equalTo("Mathematics")));
  }

  @Test
  public void testFetchNonExistentDepartmentById() throws SQLException {
    // Try to fetch a department that doesn't exist
    Department fetchedDepartment = departmentDAO.findById(999);

    // Validate that null is returned
    assertThat(fetchedDepartment, is(nullValue()));
  }

  @Test
  public void testFetchNonExistentDepartmentByName() throws SQLException {
    // Try to fetch a department that doesn't exist
    Department fetchedDepartment = departmentDAO.findByName("Nonexistent Department");

    // Validate that null is returned
    assertThat(fetchedDepartment, is(nullValue()));
  }

  @Test
  public void testFindAllDepartments() throws SQLException {
    // Create multiple departments
    Department dept1 = new Department(101, "Computer Science");
    Department dept2 = new Department(102, "Mathematics");
    Department dept3 = new Department(103, "Physics");

    // Persist all departments
    departmentDAO.save(dept1);
    departmentDAO.save(dept2);
    departmentDAO.save(dept3);

    // Fetch all departments
    List<Department> allDepartments = departmentDAO.findAll();

    // Validate using Hamcrest matchers
    assertThat(allDepartments, hasSize(3));
    assertThat(allDepartments, hasItem(hasProperty("name", is("Computer Science"))));
    assertThat(allDepartments, hasItem(hasProperty("name", is("Mathematics"))));
    assertThat(allDepartments, hasItem(hasProperty("name", is("Physics"))));
    assertThat(allDepartments, hasItem(hasProperty("id", is(101))));
    assertThat(allDepartments, hasItem(hasProperty("id", is(102))));
    assertThat(allDepartments, hasItem(hasProperty("id", is(103))));
  }

  @Test
  public void testFindAllReturnsEmptyListWhenNoDepartments() throws SQLException {
    // Fetch all departments from empty table
    List<Department> allDepartments = departmentDAO.findAll();

    // Validate that an empty list is returned
    assertThat(allDepartments, is(empty()));
  }

  @Test
  public void testDepartmentEquality() throws SQLException {
    // Create and persist a department
    Department original = new Department(104, "Engineering");
    departmentDAO.save(original);

    // Fetch the department
    Department fetched = departmentDAO.findById(104);

    // Validate that the objects are equal
    assertThat(fetched, is(equalTo(original)));
  }
}
