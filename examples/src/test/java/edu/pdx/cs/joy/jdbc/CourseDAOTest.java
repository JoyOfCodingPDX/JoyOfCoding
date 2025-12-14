package edu.pdx.cs.joy.jdbc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CourseDAOTest {

  private Connection connection;
  private CourseDAO courseDAO;

  @BeforeEach
  public void setUp() throws SQLException {
    // Create an in-memory H2 database
    connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

    // Drop the table if it exists from a previous test, then create it
    CourseDAO.dropTable(connection);
    CourseDAO.createTable(connection);

    // Initialize the DAO with the connection
    courseDAO = new CourseDAO(connection);
  }

  @AfterEach
  public void tearDown() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      // Drop the table and close the connection
      CourseDAO.dropTable(connection);
      connection.close();
    }
  }

  @Test
  public void testPersistAndFetchCourse() throws SQLException {
    // Create a course
    int csDepartmentId = 101;
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
    // Create multiple courses
    int csDepartmentId = 102;
    int mathDepartmentId = 103;
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
}
