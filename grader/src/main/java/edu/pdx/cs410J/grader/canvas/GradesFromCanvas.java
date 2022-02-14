package edu.pdx.cs410J.grader.canvas;

import edu.pdx.cs410J.grader.Assignment;
import edu.pdx.cs410J.grader.GradeBook;
import edu.pdx.cs410J.grader.Student;

import java.util.*;

public class GradesFromCanvas {
  private final List<CanvasStudent> students = new ArrayList<>();

  public static CanvasStudentBuilder newStudent() {
    return new CanvasStudentBuilder();
  }

  public void addStudent(CanvasStudent student) {
    this.students.add(student);
  }

  public Optional<Student> findStudentInGradebookForCanvasStudent(CanvasStudent canvasStudent, GradeBook book) {
    return book.studentsStream().filter((Student student) -> {
      if (haveSameCanvasId(canvasStudent, student)) {
        return true;

      } else if (studentIdIsSameAsLoginId(canvasStudent, student)) {
        student.setCanvasId(canvasStudent.getCanvasId());
        return true;

      } else if (haveSameFirstAndLastNameIgnoringCase(canvasStudent, student)) {
        student.setCanvasId(canvasStudent.getCanvasId());
        return true;

      } else {
        return false;
      }
    }).findAny();
  }

  private boolean studentIdIsSameAsLoginId(CanvasStudent d2lStudent, Student student) {
    return d2lStudent.getLoginId().equals(student.getId());
  }

  private boolean haveSameFirstAndLastNameIgnoringCase(CanvasStudent d2lStudent, Student student) {
    return d2lStudent.getFirstName().equalsIgnoreCase(student.getFirstName()) &&
      d2lStudent.getLastName().equalsIgnoreCase(student.getLastName());
  }

  private boolean haveSameCanvasId(CanvasStudent canvasStudent, Student student) {
    return canvasStudent.getCanvasId().equals(student.getCanvasId());
  }

  public List<CanvasStudent> getStudents() {
    return this.students;
  }

  public Optional<Assignment> findAssignmentInGradebookForCanvasQuiz(String canvasQuizName, GradeBook gradebook) {
    Assignment assignment = gradebook.getAssignment(canvasQuizName);
    if (assignment != null) {
      return Optional.of(assignment);
    }

    return findAssignmentInGradebookLike(canvasQuizName, gradebook);
  }

  private Optional<Assignment> findAssignmentInGradebookLike(String canvasQuizName, GradeBook gradebook) {
    for (String assignmentName : gradebook.getAssignmentNames()) {
      Assignment assignment = gradebook.getAssignment(assignmentName);
      if (canvasQuizName.startsWith(assignmentName)) {
        return Optional.ofNullable(assignment);
      }

      if (canvasQuizName.contains(assignment.getDescription())) {
        return Optional.of(assignment);
      }
    }
    return Optional.empty();
  }

  static class CanvasStudent {
    private final String firstName;
    private final String lastName;
    private final String loginId;
    private final String canvasId;
    private final String section;
    private final Map<String, Double> scores = new HashMap<>();

    public CanvasStudent(String firstName, String lastName, String loginId, String canvasId, String section) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.loginId = loginId;
      this.canvasId = canvasId;
      this.section = section;
    }

    public String getLoginId() {
      return loginId;
    }

    public String getFirstName() {
      return firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public String getCanvasId() {
      return this.canvasId;
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

    public String getSection() {
      return section;
    }

    @Override
    public String toString() {
      return "Canvas student \"" + getFirstName() + " " + getLastName() + "\" with id " + getLoginId();
    }
  }

  static class CanvasStudentBuilder {
    private String firstName;
    private String lastName;
    private String loginId;
    private String canvasId;
    private String section;

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

    public CanvasStudentBuilder setLoginId(String loginId) {
      this.loginId = loginId;
      return this;
    }

    public CanvasStudentBuilder setCanvasId(String canvasId) {
      this.canvasId = canvasId;
      return this;
    }

    public CanvasStudentBuilder setSection(String section) {
      this.section = section;
      return this;
    }

    public CanvasStudent create() {
      if (firstName == null) {
        throw new IllegalStateException("Missing first name");
      }

      if (lastName == null) {
        throw new IllegalStateException("Missing last name");
      }

      if (loginId == null) {
        throw new IllegalStateException("Missing login Id");
      }

      if (canvasId == null) {
        throw new IllegalStateException("Missing canvas Id");
      }

      if (section == null) {
        throw new IllegalStateException("Missing section");
      }

      return new CanvasStudent(firstName, lastName, loginId, canvasId, section);
    }
  }
}
