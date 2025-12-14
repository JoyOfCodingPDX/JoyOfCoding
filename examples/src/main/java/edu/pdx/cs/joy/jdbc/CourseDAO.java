package edu.pdx.cs.joy.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for managing Course entities in the database.
 * Demonstrates basic JDBC operations: CREATE, READ.
 */
public class CourseDAO {

  private final Connection connection;

  /**
   * Creates a new CourseDAO with the specified database connection.
   *
   * @param connection the database connection to use
   */
  public CourseDAO(Connection connection) {
    this.connection = connection;
  }

  /**
   * Saves a course to the database.
   *
   * @param course the course to save
   * @throws SQLException if a database error occurs
   */
  public void save(Course course) throws SQLException {
    String sql = "INSERT INTO courses (title, department_id) VALUES (?, ?)";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, course.getTitle());
      statement.setInt(2, course.getDepartmentId());
      statement.executeUpdate();
    }
  }

  /**
   * Finds a course by its title.
   *
   * @param title the title to search for
   * @return the course with the given title, or null if not found
   * @throws SQLException if a database error occurs
   */
  public Course findByTitle(String title) throws SQLException {
    String sql = "SELECT title, department_id FROM courses WHERE title = ?";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, title);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return extractCourseFromResultSet(resultSet);
        }
      }
    }

    return null;
  }

  /**
   * Finds all courses associated with a specific department.
   *
   * @param departmentId the department ID to search for
   * @return a list of courses in the department
   * @throws SQLException if a database error occurs
   */
  public List<Course> findByDepartmentId(int departmentId) throws SQLException {
    List<Course> courses = new ArrayList<>();
    String sql = "SELECT title, department_id FROM courses WHERE department_id = ?";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, departmentId);

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          courses.add(extractCourseFromResultSet(resultSet));
        }
      }
    }

    return courses;
  }

  /**
   * Extracts a Course object from the current row of a ResultSet.
   *
   * @param resultSet the result set positioned at a course row
   * @return a Course object with data from the result set
   * @throws SQLException if a database error occurs
   */
  private Course extractCourseFromResultSet(ResultSet resultSet) throws SQLException {
    String title = resultSet.getString("title");
    int departmentId = resultSet.getInt("department_id");
    return new Course(title, departmentId);
  }
}

