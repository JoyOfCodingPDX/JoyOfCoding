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
    Department department = new Department("Computer Science");
    departmentDAO.save(department);

    // Get the auto-generated department ID
    int csDepartmentId = department.getId();

    // Create a course
    String javaCourseName = "Introduction to Java";
    int credits = 4;
    Course course = new Course(javaCourseName, csDepartmentId, credits);

    // Persist the course
    courseDAO.save(course);

    // Verify that an ID was auto-generated
    int generatedId = course.getId();
    assertThat(generatedId, is(greaterThan(0)));

    // Fetch the course by title
    Course fetchedCourse = courseDAO.findByTitle(javaCourseName);

    // Validate the fetched course using Hamcrest assertions
    assertThat(fetchedCourse, is(notNullValue()));
    assertThat(fetchedCourse.getId(), is(equalTo(generatedId)));
    assertThat(fetchedCourse.getTitle(), is(equalTo(javaCourseName)));
    assertThat(fetchedCourse.getDepartmentId(), is(equalTo(csDepartmentId)));
    assertThat(fetchedCourse.getCredits(), is(equalTo(credits)));
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
    Department csDepartment = new Department("Computer Science");
    Department mathDepartment = new Department("Mathematics");

    departmentDAO.save(csDepartment);
    departmentDAO.save(mathDepartment);

    // Get the auto-generated department IDs
    int csDepartmentId = csDepartment.getId();
    int mathDepartmentId = mathDepartment.getId();

    // Create multiple courses
    String dataStructuresName = "Data Structures";
    String algorithmsName = "Algorithms";
    String calculusName = "Calculus";

    Course course1 = new Course(dataStructuresName, csDepartmentId, 4);
    Course course2 = new Course(algorithmsName, csDepartmentId, 3);
    Course course3 = new Course(calculusName, mathDepartmentId, 4);

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
    Course course = new Course("Database Systems", 999, 3);

    // Attempting to save should throw an SQLException due to foreign key constraint
    SQLException exception = assertThrows(SQLException.class, () -> {
      courseDAO.save(course);
    });
    assertThat(exception.getMessage(), containsString("Referential integrity"));
  }

  @Test
  public void testCreditsArePersisted() throws SQLException {
    // Create and persist a department first (required for foreign key)
    Department department = new Department("Mathematics");
    departmentDAO.save(department);
    int deptId = department.getId();

    // Create courses with different credit values
    Course threeCredits = new Course("Statistics", deptId, 3);
    Course fourCredits = new Course("Linear Algebra", deptId, 4);
    Course fiveCredits = new Course("Abstract Algebra", deptId, 5);

    // Persist all courses
    courseDAO.save(threeCredits);
    courseDAO.save(fourCredits);
    courseDAO.save(fiveCredits);

    // Fetch the courses and verify credits
    Course fetchedThree = courseDAO.findByTitle("Statistics");
    Course fetchedFour = courseDAO.findByTitle("Linear Algebra");
    Course fetchedFive = courseDAO.findByTitle("Abstract Algebra");

    assertThat(fetchedThree.getCredits(), is(equalTo(3)));
    assertThat(fetchedFour.getCredits(), is(equalTo(4)));
    assertThat(fetchedFive.getCredits(), is(equalTo(5)));
  }

  @Test
  public void testUpdateCourse() throws SQLException {
    // Create and persist a department first (required for foreign key)
    Department department = new Department("Computer Science");
    departmentDAO.save(department);
    int deptId = department.getId();

    // Create and persist a course
    Course course = new Course("Database Systems", deptId, 3);
    courseDAO.save(course);

    int courseId = course.getId();
    assertThat(courseId, is(greaterThan(0)));

    // Update the course
    course.setTitle("Advanced Database Systems");
    course.setCredits(4);
    courseDAO.update(course);

    // Fetch the course and verify it was updated
    Course updatedCourse = courseDAO.findByTitle("Advanced Database Systems");
    assertThat(updatedCourse, is(notNullValue()));
    assertThat(updatedCourse.getId(), is(equalTo(courseId)));
    assertThat(updatedCourse.getTitle(), is(equalTo("Advanced Database Systems")));
    assertThat(updatedCourse.getCredits(), is(equalTo(4)));

    // Verify the old title doesn't exist anymore
    Course oldCourse = courseDAO.findByTitle("Database Systems");
    assertThat(oldCourse, is(nullValue()));
  }

}
