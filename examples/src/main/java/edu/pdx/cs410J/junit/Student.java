package edu.pdx.cs410J.junit;

import java.util.*;

/**
 * This class represents a student that may enroll in a section of a
 * course.
 */
public class Student {

  /** The student's id */
  private String id;

  /** The grades that the student received in the sections of courses
   * that he was enrolled in */
  private Map<Section, Grade> grades;

  /////////////////////////  Constructors  //////////////////////////

  /**
   * Creates a new <code>Student</code> with a given id
   */
  public Student(String id) {
    this.id = id;
    this.grades = new HashMap<Section, Grade>();
  }

  //////////////////////  Accessor Methods  ////////////////////////

  /**
   * Sets the grade this student got in a given section of a course
   */
  public void setGrade(Section section, Grade grade) {
    this.grades.put(section, grade);
  }

  /**
   * Returns the grade this student received in a given sesion of a
   * course.  Returns <code>null</code> if the student was never
   * enrolled in the course or is no grade was assigned.
   */
  public Grade getGrade(Section section) {
    return (Grade) this.grades.get(section);
  }

  /**
   * Returns the grades that this student received 
   */
  public Map<Section, Grade> getGrades() {
    return grades;
  }

  /**
   * Returns the id of this student
   */
  public String getId() {
    return id;
  }
}
