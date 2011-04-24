package edu.pdx.cs410J.junit;

/**
 * This class represent a course taught at a university.
 */
public class Course {

  /** The name of the department in which this course is taught */
  private String department;

  /** The number of the course */
  private int number;

  /** The number of credits for this course */
  private int credits;

  ////////////////////////  Constructors  /////////////////////////

  /**
   * Creates a new <code>Course</code> with a given name, taught in a
   * given department, and for a given number of credits
   *
   * @throws IllegalArgumentException
   *         <code>number</code> is less than 100 or credits is less
   *         than 0
   */
  public Course(String department, int number, int credits) {
    if (number < 100) {
      String s = "A course number (" + number + 
        ") must be greater than 100";
      throw new IllegalArgumentException(s);
    }

    this.department = department;
    this.number = number;
    this.setCredits(credits);
  }

  /////////////////////  Accessor Methods  ////////////////////////

  /**
   * Sets the number of credits that this course is worth
   */
  public void setCredits(int credits) {
    if (credits < 0) {
      String s = "A course cannot be taken for a negative number " +
        "of credits: " + credits;
      throw new IllegalArgumentException(s);
    }

    this.credits = credits;
  }

  /**
   * Returns the number of credits that this course is worth
   */
  public int getCredits() {
    return this.credits;
  }

  /**
   * Returns the department that offers this course
   */
  public String getDepartment() {
    return department;
  }

  /**
   * Returns the number of this course
   */
  public int getNumber() {
    return number;
  }

}
