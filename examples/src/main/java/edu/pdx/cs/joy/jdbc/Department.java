package edu.pdx.cs.joy.jdbc;

/**
 * Represents a department in a college course catalog.
 * Each department has a unique ID and a name.
 */
public class Department {
  private int id;
  private String name;

  /**
   * Creates a new Department with the specified ID and name.
   *
   * @param id the unique identifier for the department
   * @param name the name of the department
   */
  public Department(int id, String name) {
    this.id = id;
    this.name = name;
  }

  /**
   * Creates a new Department with the specified name.
   * The ID will be auto-generated when the department is saved to the database.
   *
   * @param name the name of the department
   */
  public Department(String name) {
    this.name = name;
  }

  /**
   * Creates a new Department with no initial values.
   * Useful for frameworks that use reflection.
   */
  public Department() {
  }

  /**
   * Returns the ID of this department.
   *
   * @return the department ID
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the ID of this department.
   *
   * @param id the department ID
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Returns the name of this department.
   *
   * @return the department name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of this department.
   *
   * @param name the department name
   */
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Department{" +
      "id=" + id +
      ", name='" + name + '\'' +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Department that = (Department) o;

    if (id != that.id) return false;
    return name != null ? name.equals(that.name) : that.name == null;
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
  }
}

