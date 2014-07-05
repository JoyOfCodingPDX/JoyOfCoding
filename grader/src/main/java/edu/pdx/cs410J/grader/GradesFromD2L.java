package edu.pdx.cs410J.grader;

import java.util.ArrayList;
import java.util.List;

public class GradesFromD2L {
  private List<D2LStudent> students = new ArrayList<D2LStudent>();

  public static D2LStudentBuilder newStudent() {
    return new D2LStudentBuilder();
  }

  public void addStudent(D2LStudent student) {
    this.students.add(student);
  }

  public Student findStudentInGradebookForD2LStudent(D2LStudent d2lStudent, GradeBook book) {
    for (String studentId : book.getStudentIds()) {
      Student student = book.getStudent(studentId);
      if (haveSameD2LId(d2lStudent, student)) {
        return student;
      }
    }

    return null;
  }

  private boolean haveSameD2LId(D2LStudent d2lStudent, Student student) {
    return d2lStudent.getD2lId().equals(student.getD2LId());
  }

  static class D2LStudent {
    private final String firstName;
    private final String lastName;
    private final String d2lId;

    public D2LStudent(String firstName, String lastName, String d2lId) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.d2lId = d2lId;
    }

    public String getD2lId() {
      return d2lId;
    }
  }

  static class D2LStudentBuilder {
    private String firstName;
    private String lastName;
    private String d2lId;

    public D2LStudentBuilder setFirstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public D2LStudentBuilder setLastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public D2LStudentBuilder setD2LId(String d2lId) {
      this.d2lId = d2lId;
      return this;
    }

    public D2LStudent create() {
      if (firstName == null) {
        throw new IllegalStateException("Missing first name");
      }

      if (lastName == null) {
        throw new IllegalStateException("Missing last name");
      }

      if (d2lId == null) {
        throw new IllegalStateException("Missing d2l Id");
      }

      return new D2LStudent(firstName, lastName, d2lId);
    }
  }
}
