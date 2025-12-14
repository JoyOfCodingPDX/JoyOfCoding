package edu.pdx.cs.joy.jdbc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CourseDAOTest {

  private Connection connection;
  private CourseDAO courseDAO;
  private DepartmentDAO departmentDAO;

  @BeforeEach
  public void setUp() throws SQLException {
    // Create an in-memory H2 database
    connection = H2DatabaseHelper.createInMemoryConnection("test");

    // Drop tables if they exist from a previous test, then create them
    // Note: Must drop courses first due to foreign key constraint
    CourseDAO.dropTable(connection);
    DepartmentDAO.dropTable(connection);

    // Create departments table first, then courses (due to foreign key)
    DepartmentDAO.createTable(connection);
    CourseDAO.createTable(connection);

    // Initialize the DAOs with the connection
    courseDAO = new CourseDAO(connection);
    departmentDAO = new DepartmentDAO(connection);
  }

  @AfterEach
  public void tearDown() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      // Drop tables and close the connection
      // Note: Must drop courses first due to foreign key constraint
      CourseDAO.dropTable(connection);
      DepartmentDAO.dropTable(connection);
      connection.close();
    }
  }

  @Test
  public void testPersistAndFetchCourse() throws SQLException {
    // Create and persist a department first (required for foreign key)
    int csDepartmentId = 101;
    Department department = new Department(csDepartmentId, "Computer Science");
    departmentDAO.save(department);

    // Create a course
    String javaCourseName = "Introduction to Java";
    Course course = new Course(javaCourseName, csDepartmentId);

    // Persist the course
    courseDAO.save(course);

    // Fetch the course by title
    Course fetchedCourse = courseDAO.findByTitle(javaCourseName);

    // Validate the fetched course using Hamcrest assertions
    assertThat(fetchedCourse, is(notNullValue()));
    assertThat(fetchedCourse.getTitle(), is(equalTo(javaCourseName)));
    assertThat(fetchedCourse.getDepartmentId(), is(equalTo(csDepartmentId)));
  }

  @Test
  public void testFetchNonExistentCourse() throws SQLException {
    // Try to fetch a course that doesn't exist
    Course fetchedCourse = courseDAO.findByTitle("Nonexistent Course");

    // Validate that null is returned
    assertThat(fetchedCourse, is(nullValue()));
  }

  @Test
  public void testPersistMultipleCourses() throws SQLException {
    // Create and persist departments first (required for foreign key)
    int csDepartmentId = 102;
    int mathDepartmentId = 103;

    Department csDepartment = new Department(csDepartmentId, "Computer Science");
    Department mathDepartment = new Department(mathDepartmentId, "Mathematics");
    departmentDAO.save(csDepartment);
    departmentDAO.save(mathDepartment);

    // Create multiple courses
    String dataStructuresName = "Data Structures";
    String algorithmsName = "Algorithms";
    String calculusName = "Calculus";

    Course course1 = new Course(dataStructuresName, csDepartmentId);
    Course course2 = new Course(algorithmsName, csDepartmentId);
    Course course3 = new Course(calculusName, mathDepartmentId);

    // Persist all courses
    courseDAO.save(course1);
    courseDAO.save(course2);
    courseDAO.save(course3);

    // Fetch courses by department
    List<Course> coursesByDept102 = courseDAO.findByDepartmentId(csDepartmentId);
    List<Course> coursesByDept103 = courseDAO.findByDepartmentId(mathDepartmentId);

    // Validate using Hamcrest matchers
    assertThat(coursesByDept102, hasSize(2));
    assertThat(coursesByDept102, hasItem(hasProperty("title", is(dataStructuresName))));
    assertThat(coursesByDept102, hasItem(hasProperty("title", is(algorithmsName))));

    assertThat(coursesByDept103, hasSize(1));
    assertThat(coursesByDept103, hasItem(hasProperty("title", is(calculusName))));
  }

  @Test
  public void testForeignKeyConstraintPreventsInvalidDepartmentId() {
    // Try to create a course with a non-existent department ID
    Course course = new Course("Database Systems", 999);

    // Attempting to save should throw an SQLException due to foreign key constraint
    SQLException exception = assertThrows(SQLException.class, () -> {
      courseDAO.save(course);
    });
    assertThat(exception.getMessage(), containsString("Referential integrity"));
  }

}
