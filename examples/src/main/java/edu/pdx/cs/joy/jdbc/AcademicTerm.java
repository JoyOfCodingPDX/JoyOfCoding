package edu.pdx.cs.joy.jdbc;

import java.time.LocalDate;

/**
 * Represents an academic term (such as Fall 2024 or Spring 2025) during which courses are offered.
 * Each term has a unique ID, a name, and start and end dates.
 */
public class AcademicTerm {
  private int id;
  private String name;
  private LocalDate startDate;
  private LocalDate endDate;

  /**
   * Creates a new AcademicTerm with the specified name, start date, and end date.
   *
   * @param name the name of the term (e.g., "Fall 2024")
   * @param startDate the start date of the term
   * @param endDate the end date of the term
   */
  public AcademicTerm(String name, LocalDate startDate, LocalDate endDate) {
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  /**
   * Creates a new AcademicTerm with the specified ID, name, start date, and end date.
   *
   * @param id the unique identifier for the term
   * @param name the name of the term (e.g., "Fall 2024")
   * @param startDate the start date of the term
   * @param endDate the end date of the term
   */
  public AcademicTerm(int id, String name, LocalDate startDate, LocalDate endDate) {
    this.id = id;
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  /**
   * Creates a new AcademicTerm with no initial values.
   * Useful for frameworks that use reflection.
   */
  public AcademicTerm() {
  }

  /**
   * Returns the unique ID of this academic term.
   *
   * @return the term ID
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the unique ID of this academic term.
   *
   * @param id the term ID
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Returns the name of this academic term.
   *
   * @return the term name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of this academic term.
   *
   * @param name the term name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the start date of this academic term.
   *
   * @return the start date
   */
  public LocalDate getStartDate() {
    return startDate;
  }

  /**
   * Sets the start date of this academic term.
   *
   * @param startDate the start date
   */
  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  /**
   * Returns the end date of this academic term.
   *
   * @return the end date
   */
  public LocalDate getEndDate() {
    return endDate;
  }

  /**
   * Sets the end date of this academic term.
   *
   * @param endDate the end date
   */
  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  @Override
  public String toString() {
    return "AcademicTerm{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", startDate=" + startDate +
      ", endDate=" + endDate +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AcademicTerm that = (AcademicTerm) o;

    if (id != that.id) return false;
    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
    return endDate != null ? endDate.equals(that.endDate) : that.endDate == null;
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
    result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
    return result;
  }
}

