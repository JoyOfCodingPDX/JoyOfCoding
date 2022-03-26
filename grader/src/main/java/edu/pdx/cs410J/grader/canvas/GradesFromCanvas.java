package edu.pdx.cs410J.grader.canvas;

import edu.pdx.cs410J.grader.Assignment;
import edu.pdx.cs410J.grader.GradeBook;
import edu.pdx.cs410J.grader.Student;

import java.util.*;
import java.util.stream.Collectors;

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
        noteStudent(canvasStudent, student, book);
        return true;

      } else if (haveSameFirstAndLastNameIgnoringCase(canvasStudent, student)) {
        noteStudent(canvasStudent, student, book);
        return true;

      } else {
        return false;
      }
    }).findAny();
  }

  private void noteStudent(CanvasStudent canvasStudent, Student student, GradeBook book) {
    student.setCanvasId(canvasStudent.getCanvasId());
    book.setSectionName(student.getEnrolledSection(), canvasStudent.getSection());
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
    Optional<Assignment> maybeAssignment;

    Assignment assignment = gradebook.getAssignment(canvasQuizName);
    if (assignment != null) {
      maybeAssignment = Optional.of(assignment);

    } else {
      maybeAssignment = findAssignmentInGradebookLike(canvasQuizName, gradebook);
    }

    if (maybeAssignment.isPresent()) {
      assignment = maybeAssignment.get();

    }

    return maybeAssignment;
  }

  private Optional<Assignment> findAssignmentInGradebookLike(String canvasQuizName, GradeBook gradebook) {
    List<Assignment> assignments = new ArrayList<>();
    for (String assignmentName : gradebook.getAssignmentNames()) {
      Assignment assignment = gradebook.getAssignment(assignmentName);
      if (canvasQuizName.startsWith(assignmentName)) {
        assignments.add(assignment);
      }

      if (canvasQuizName.contains(assignment.getDescription())) {
        assignments.add(assignment);
      }
    }

    if (assignments.isEmpty()) {
      return Optional.empty();

    } else if (assignments.size() == 1) {
      return Optional.of(assignments.get(0));

    } else {
      String message = "Multiple assignments match \"" + canvasQuizName + "\": " +
        assignments.stream().map(Assignment::getName).collect(Collectors.joining(", "));
      throw new IllegalStateException(message);
    }
  }

  static class CanvasStudent {
    private final String firstName;
    private final String lastName;
    private final String loginId;
    private final String canvasId;
    private final String section;
    private final Map<CanvasAssignment, Double> scores = new HashMap<>();

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

    public void setScore(CanvasAssignment assignment, Double score) {
      this.scores.put(assignment, score);
    }

    public Double getScore(CanvasAssignment assignment) {
      return this.scores.get(assignment);
    }

    public Iterable<CanvasAssignment> getAssignments() {
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

  public static class CanvasAssignment {
    private final String name;
    private final int id;
    private double pointsPossible;

    public CanvasAssignment(String name, int id) {
      this.name = name;
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public int getId() {
      return id;
    }

    public double getPointsPossible() {
      return pointsPossible;
    }

    void setPossiblePoints(double possiblePoints) {
      this.pointsPossible = possiblePoints;
    }
  }
}
