package edu.pdx.cs410J.j2se15;

import java.util.*;

/**
 * This class demonstrates how updated domain classes that use
 * generics can interact with legacy code that doesn't use generics.
 *
 * @author David Whitlock
 * @since Winter 2005
 */
public class GenericsAndLegacy2 {

  /**
   * An updated domain class that uses generics
   */
  static class Course {
    private List<Student> allStudents =
      new ArrayList<Student>();

    void addStudents(Collection<Student> students) {
      for (Student s : students) {
        this.allStudents.add(s);
      }
    }

    List<Student> getStudents() {
      return this.allStudents;
    }  
  }

  /**
   * A student
   */
  static class Student {

  }

  /**
   * A grad student
   */
  static class GradStudent extends Student {

  }

  /**
   * A "legacy" main program that interacts with an updated domain
   * class that uses generic collections.
   */
  public static void main(String[] args) {
    Course course = new Course();
    Collection students = new ArrayList();
    students.add(new Student());
    students.add(new GradStudent());
    course.addStudents(students);  // warning

    Student student1 = course.getStudents().get(0);
  }

}
