package edu.pdx.cs410J.grader;

import java.util.*;

public class GradesFromCanvas {
  private List<CanvasStudent> students = new ArrayList<>();

  public static CanvasStudentBuilder newStudent() {
    return new CanvasStudentBuilder();
  }

  public void addStudent(CanvasStudent student) {
    this.students.add(student);
  }

  public Optional<Student> findStudentInGradebookForCanvasStudent(CanvasStudent d2lStudent, GradeBook book) {
    return book.studentsStream().filter((Student student) -> {
      if (haveSameD2LId(d2lStudent, student)) {
        return true;

      } else if (studentIdIsSameAsD2LId(d2lStudent, student)) {
        student.setD2LId(d2lStudent.getLoginId());
        return true;

      } else if (haveSameFirstAndLastNameIgnoringCase(d2lStudent, student)) {
        student.setD2LId(d2lStudent.getLoginId());
        return true;

      } else {
        return false;
      }
    }).findAny();
  }

  private boolean studentIdIsSameAsD2LId(CanvasStudent d2lStudent, Student student) {
    return d2lStudent.getLoginId().equals(student.getId());
  }

  private boolean haveSameFirstAndLastNameIgnoringCase(CanvasStudent d2lStudent, Student student) {
    return d2lStudent.getFirstName().equalsIgnoreCase(student.getFirstName()) &&
      d2lStudent.getLastName().equalsIgnoreCase(student.getLastName());
  }

  private boolean haveSameD2LId(CanvasStudent d2lStudent, Student student) {
    return d2lStudent.getLoginId().equals(student.getD2LId());
  }

  public List<CanvasStudent> getStudents() {
    return this.students;
  }

  public Optional<Assignment> findAssignmentInGradebookForCanvasQuiz(String quizName, GradeBook gradebook) {
    Assignment assignment = gradebook.getAssignment(quizName);
    if (assignment != null) {
      return Optional.ofNullable(assignment);
    }

    return findAssignmentInGradebookLike(quizName, gradebook);
  }

  private Optional<Assignment> findAssignmentInGradebookLike(String quizName, GradeBook gradebook) {
    for (String assignmentName : gradebook.getAssignmentNames()) {
      Assignment assignment = gradebook.getAssignment(assignmentName);
      if (quizName.startsWith(assignmentName)) {
        return Optional.ofNullable(assignment);
      }

      if (quizName.startsWith(assignment.getDescription())) {
        return Optional.of(assignment);
      }
    }
    return Optional.empty();
  }

  public static class CanvasStudent {
    private final String firstName;
    private final String lastName;
    private final String d2lId;
    private Map<String, Double> scores = new HashMap<>();

    public CanvasStudent(String firstName, String lastName, String d2lId) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.d2lId = d2lId;
    }

    public String getLoginId() {
      return d2lId;
    }

    public String getFirstName() {
      return firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public void setScore(String quizName, double score) {
      this.scores.put(quizName, score);
    }

    public Double getScore(String quizName) {
      return this.scores.get(quizName);
    }

    public Iterable<String> getAssignmentNames() {
      return this.scores.keySet();
    }

    @Override
    public String toString() {
      return "D2L student \"" + getFirstName() + " " + getLastName() + "\" with id " + getLoginId();
    }
  }

  public static class CanvasStudentBuilder {
    private String firstName;
    private String lastName;
    private String d2lId;

    private CanvasStudentBuilder() {

    }

    public CanvasStudentBuilder setFirstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public CanvasStudentBuilder setLastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public CanvasStudentBuilder setLoginId(String d2lId) {
      this.d2lId = d2lId;
      return this;
    }

    public CanvasStudent create() {
      if (firstName == null) {
        throw new IllegalStateException("Missing first name");
      }

      if (lastName == null) {
        throw new IllegalStateException("Missing last name");
      }

      if (d2lId == null) {
        throw new IllegalStateException("Missing d2l Id");
      }

      return new CanvasStudent(firstName, lastName, d2lId);
    }
  }
}
