package edu.pdx.cs.joy.jdbc;

import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CourseDAOIT {

  private static final String TEST_COURSE_TITLE = "Advanced JDBC Programming";
  private static final int TEST_DEPARTMENT_ID = 201;

  private static String dbFilePath;
  private Connection connection;
  private CourseDAO courseDAO;

  @BeforeAll
  public static void createTable() throws SQLException {
    // Create database file in temporary directory
    String tempDir = System.getProperty("java.io.tmpdir");
    dbFilePath = tempDir + File.separator + "CourseDAOIT.db";

    // Connect to the file-based H2 database
    Connection connection = H2DatabaseHelper.createFileBasedConnection(dbFilePath);

    // Create the courses table
    CourseDAO.createTable(connection);

    connection.close();
  }

  @BeforeEach
  public void setUp() throws SQLException {
    // Connect to the existing database file
    connection = H2DatabaseHelper.createFileBasedConnection(dbFilePath);
    courseDAO = new CourseDAO(connection);
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
    CourseDAO.dropTable(connection);
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
  public void testPersistCourse() throws SQLException {
    // Create and persist a course
    Course course = new Course(TEST_COURSE_TITLE, TEST_DEPARTMENT_ID);
    courseDAO.save(course);

    // Verify the course was saved by fetching it in the same test
    Course fetchedCourse = courseDAO.findByTitle(TEST_COURSE_TITLE);
    assertThat(fetchedCourse, is(notNullValue()));
    assertThat(fetchedCourse.getTitle(), is(equalTo(TEST_COURSE_TITLE)));
    assertThat(fetchedCourse.getDepartmentId(), is(equalTo(TEST_DEPARTMENT_ID)));
  }

  @Test
  @Order(2)
  public void testFindPersistedCourse() throws SQLException {
    // Search for the course that was persisted in the previous test
    Course fetchedCourse = courseDAO.findByTitle(TEST_COURSE_TITLE);

    // Validate that the course persisted between test methods
    assertThat(fetchedCourse, is(notNullValue()));
    assertThat(fetchedCourse.getTitle(), is(equalTo(TEST_COURSE_TITLE)));
    assertThat(fetchedCourse.getDepartmentId(), is(equalTo(TEST_DEPARTMENT_ID)));
  }
}
