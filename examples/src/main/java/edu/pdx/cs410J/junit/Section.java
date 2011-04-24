package edu.pdx.cs410J.junit;

import java.util.*;

/**
 * This class represents a section of a course being offered in a
 * given term during a given year.
 */
public class Section {

  /** The winter term */
  public static final int WINTER = 0;

  /** The spring term */
  public static final int SPRING = 1;

  /** The summer term */
  public static final int SUMMER = 2;

  /** The fall term */
  public static final int FALL = 3;

  /////////////////////  Instance Fields  ////////////////////////

  /** The course being offered */
  private Course course;

  /** The term in which section is offered */
  private int term;

  /** The year in which the section is offered */
  private int year;

  /** The student enrolled in the course */
  private Set<Student> students;

  //////////////////////  Constructors  //////////////////////////

  /**
   * Creates a new section of a course being offered in the given term
   * and year.  Initially, no students are enrolled.
   *
   * @throws IllegalArgumentException
   *         If the <code>term</code> is not one of {@link #WINTER},
   *         {@link #SPRING}, {@link #SUMMER}, or {@link #FALL}.
   *         (Great, now I've got that James Taylor song going through
   *         my head.) 
   */
  public Section(Course course, int term, int year) {
    if (term != WINTER && term != SPRING && term != SUMMER && 
        term != FALL) {
      String s = "Invalid term code: " + term;
      throw new IllegalArgumentException(s);
    }

    this.course = course;
    this.term = term;
    this.year = year;
    this.students = new HashSet<Student>();
  }

  /////////////////////  Accessor Methods  ////////////////////////

  /**
   * Enrolls a student in this section
   */
  public void addStudent(Student student) {
    this.students.add(student);
  }

  /**
   * Drops a student from this section
   *
   * @throws IllegalArgumentException
   *         The <code>student</code> is not enrolled in this section
   */
  public void dropStudent(Student student) {
    if (!this.students.remove(student)) {
      String s = "Student " + student + " is not enrolled in " + this;
      throw new IllegalArgumentException(s);
    }
  }

  /**
   * Returns the number of students enrolled in this section
   */
  public int getClassSize() {
    return this.students.size();
  }

  /**
   * Returns the course being offered
   */
  public Course getCourse() {
    return course;
  }

  /**
   * Returns the students enrolled in this section
   */
  public Set<Student> getStudents() {
    return students;
  }

  /**
   * Returns the term in which this section is offered
   */
  public int getTerm() {
    return term;
  }

  /**
   * Returns the year in which this section is offered
   */
  public int getYear() {
    return year;
  }

}
