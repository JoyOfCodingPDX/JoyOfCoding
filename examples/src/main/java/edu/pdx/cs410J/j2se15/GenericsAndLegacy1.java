package edu.pdx.cs410J.j2se15;

import java.util.*;

/**
 * This class demonstrates how generic collections can be used with
 * legacy code that does not use generics.
 *
 * @author David Whitlock
 * @since Winter 2005
 */
public class GenericsAndLegacy1 {

  /**
   * A "legacy" class that does not use generic collections.
   */
  static class Course {
    private List allStudents = new ArrayList();

    void addStudents(Collection students) {
      for (Iterator iter = students.iterator(); 
           iter.hasNext(); ) {
        this.allStudents.add((Student) iter.next());
      }
    }

    List getStudents() {
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
   * A main program that uses generic collections with legacy code.
   */
  public static void main(String[] args) {
    Course course = new Course();

    Collection<Student> students = 
      new ArrayList<Student>();
    students.add(new Student());
    students.add(new GradStudent());
    course.addStudents(students);

    Student student1 = (Student) course.getStudents().get(0);
  }

  /**
   * A little method that shows that aliasing arrays of parameterized
   * types are not allowed.  This code does not compile.
   */
//   private static void aliasingParameterArrays() {
//     List<String>[] lsa = new List<String>[10]; // bad
//     Object o = lsa;
//     Object[] oa = (Object[]) o;
//     List<Integer> ints = new ArrayList<Integer>();
//     ints.add(new Integer(42));
//     oa[1] = ints;  // No compiler warning, runtime okay
//     String s = lsa[1].get(0);  // ClassCastException
//   }

}
