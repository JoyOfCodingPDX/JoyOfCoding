package edu.pdx.cs.joy.jdbc;

import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DepartmentDAOIT {

  private static final String TEST_DEPARTMENT_NAME = "Computer Science";
  private static int generatedDepartmentId;

  private static String dbFilePath;
  private Connection connection;
  private DepartmentDAO departmentDAO;

  @BeforeAll
  public static void createTable() throws SQLException {
    // Create database file in temporary directory
    String tempDir = System.getProperty("java.io.tmpdir");
    dbFilePath = tempDir + File.separator + "DepartmentDAOIT.db";

    // Connect to the file-based H2 database
    Connection connection = H2DatabaseHelper.createFileBasedConnection(dbFilePath);

    // Create the departments table
    DepartmentDAO.createTable(connection);

    connection.close();
  }

  @BeforeEach
  public void setUp() throws SQLException {
    // Connect to the existing database file
    connection = H2DatabaseHelper.createFileBasedConnection(dbFilePath);
    departmentDAO = new DepartmentDAO(connection);
  }

  @AfterEach
  public void tearDown() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }

  @AfterAll
  public static void cleanUp() throws SQLException {
    // Connect one final time to drop the table and clean up
    Connection connection = H2DatabaseHelper.createFileBasedConnection(dbFilePath);
    DepartmentDAO.dropTable(connection);
    connection.close();

    // Delete the database files
    deleteIfExists(new File(dbFilePath + ".mv.db"));
    deleteIfExists(new File(dbFilePath + ".trace.db"));
  }

  private static void deleteIfExists(File file) {
    if (file.exists()) {
      assertThat(file.delete(), is(true));
    }
  }

  @Test
  @Order(1)
  public void testPersistDepartment() throws SQLException {
    // Create and persist a department (ID will be auto-generated)
    Department department = new Department(TEST_DEPARTMENT_NAME);
    departmentDAO.save(department);

    // Store the auto-generated ID for use in subsequent tests
    generatedDepartmentId = department.getId();

    // Verify that an ID was auto-generated
    assertThat(generatedDepartmentId, is(greaterThan(0)));

    // Verify the department was saved by fetching it in the same test
    Department fetchedDepartment = departmentDAO.findById(generatedDepartmentId);
    assertThat(fetchedDepartment, is(notNullValue()));
    assertThat(fetchedDepartment.getId(), is(equalTo(generatedDepartmentId)));
    assertThat(fetchedDepartment.getName(), is(equalTo(TEST_DEPARTMENT_NAME)));
  }

  @Test
  @Order(2)
  public void testFindPersistedDepartment() throws SQLException {
    // Search for the department that was persisted in the previous test
    // using the auto-generated ID
    Department fetchedDepartment = departmentDAO.findById(generatedDepartmentId);

    // Validate that the department persisted between test methods
    assertThat(fetchedDepartment, is(notNullValue()));
    assertThat(fetchedDepartment.getId(), is(equalTo(generatedDepartmentId)));
    assertThat(fetchedDepartment.getName(), is(equalTo(TEST_DEPARTMENT_NAME)));
  }
}
