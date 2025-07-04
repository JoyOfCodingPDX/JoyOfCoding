package edu.pdx.cs.joy.grader;

import edu.pdx.cs.joy.grader.gradebook.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;

public class GenerateStudentInitialsFile {

  public static List<String> generateInitials(Stream<Student> students) {
    List<StudentInitials> studentInitials = students
      .sorted(GenerateStudentInitialsFile::sortStudentsByLastThenFirstName)
      .map(StudentInitials::new)
      .collect(toCollection(ArrayList::new));
    return studentInitials.stream()
      .map(StudentInitials::getInitials)
      .collect(toCollection(ArrayList::new));
  }

  private static int sortStudentsByLastThenFirstName(Student s1, Student s2) {
    int lastNameComparison = s1.getLastName().compareToIgnoreCase(s2.getLastName());
    if (lastNameComparison != 0) {
      return lastNameComparison;
    }
    return s1.getFirstName().compareToIgnoreCase(s2.getFirstName());
  }

  private static class StudentInitials {
    private final Student student;
    int uniqueCharsOfFirstname = 1;
    int uniqueCharsOfLastname = 1;

    public StudentInitials(Student student) {
      this.student = student;
    }

    public String getInitials() {
      return student.getFirstName().substring(0, uniqueCharsOfFirstname).toUpperCase()
        + student.getLastName().substring(0, uniqueCharsOfLastname).toUpperCase();
    }
  }
}
