package edu.pdx.cs.joy.grader;

import edu.pdx.cs.joy.grader.gradebook.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;

public class GenerateStudentInitialsFile {
  public static String computeInitials(Student student) {
    return String.valueOf(getInitial(student.getFirstName())) +
      getInitial(student.getLastName());
  }

  private static char getInitial(String word) {
    return Character.toUpperCase(word.charAt(0));
  }

  public static List<String> generateInitials(Stream<Student> students) {
    Stream<Student> studentsByName = students
      .sorted(GenerateStudentInitialsFile::sortStudentsByLastThenFirstName);
    return studentsByName.map(GenerateStudentInitialsFile::computeInitials)
      .collect(toCollection(ArrayList::new));
  }

  private static int sortStudentsByLastThenFirstName(Student s1, Student s2) {
    int lastNameComparison = s1.getLastName().compareToIgnoreCase(s2.getLastName());
    if (lastNameComparison != 0) {
      return lastNameComparison;
    }
    return s1.getFirstName().compareToIgnoreCase(s2.getFirstName());
  }
}
