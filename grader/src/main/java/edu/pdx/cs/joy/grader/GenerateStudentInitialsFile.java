package edu.pdx.cs.joy.grader;

import edu.pdx.cs.joy.grader.gradebook.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;

public class GenerateStudentInitialsFile {

  public static List<String> generateInitials(Stream<Student> students) {
    return students
      .map(StudentInitials::new)
      .sorted()
      .map(StudentInitials::getInitials)
      .collect(toCollection(ArrayList::new));
  }

  private static class StudentInitials implements Comparable<StudentInitials> {
    private final Student student;
    int uniqueCharsOfFirstname = 1;
    int uniqueCharsOfLastname = 1;

    public StudentInitials(Student student) {
      this.student = student;
    }

    public String getInitials() {
      return getFirstNameInitials() + getLastNameInitials();
    }

    private String getLastNameInitials() {
      return getNameInitials(student::getLastName, uniqueCharsOfLastname);
    }

    private static String getNameInitials(Supplier<String> nameAccessor, int uniqueChars) {
      return nameAccessor.get().substring(0, uniqueChars).toUpperCase();
    }

    private String getFirstNameInitials() {
      return getNameInitials(student::getFirstName, uniqueCharsOfFirstname);
    }

    @Override
    public int compareTo(StudentInitials other) {
      int lastInitialsComparison = this.getLastNameInitials().compareToIgnoreCase(other.getLastNameInitials());
      if (lastInitialsComparison != 0) {
        return lastInitialsComparison;
      }
      return this.getFirstNameInitials().compareToIgnoreCase(other.getFirstNameInitials());
    }
  }
}
