package edu.pdx.cs.joy.jdbc;

/**
 * Represents a course in a college course catalog.
 * Each course has a title and is associated with a department.
 */
public class Course {
  private String title;
  private int departmentId;

  /**
   * Creates a new Course with the specified title and department ID.
   *
   * @param title the title of the course
   * @param departmentId the numeric ID of the department offering this course
   */
  public Course(String title, int departmentId) {
    this.title = title;
    this.departmentId = departmentId;
  }

  /**
   * Creates a new Course with no initial values.
   * Useful for frameworks that use reflection.
   */
  public Course() {
  }

  /**
   * Returns the title of this course.
   *
   * @return the course title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title of this course.
   *
   * @param title the course title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Returns the department ID for this course.
   *
   * @return the department ID
   */
  public int getDepartmentId() {
    return departmentId;
  }

  /**
   * Sets the department ID for this course.
   *
   * @param departmentId the department ID
   */
  public void setDepartmentId(int departmentId) {
    this.departmentId = departmentId;
  }

  @Override
  public String toString() {
    return "Course{" +
      "title='" + title + '\'' +
      ", departmentId=" + departmentId +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Course course = (Course) o;

    if (departmentId != course.departmentId) return false;
    return title != null ? title.equals(course.title) : course.title == null;
  }

  @Override
  public int hashCode() {
    int result = title != null ? title.hashCode() : 0;
    result = 31 * result + departmentId;
    return result;
  }
}
