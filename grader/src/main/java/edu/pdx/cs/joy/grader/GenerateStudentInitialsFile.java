package edu.pdx.cs.joy.grader;

import edu.pdx.cs.joy.grader.gradebook.Student;

public class GenerateStudentInitialsFile {
  public static String computeInitials(Student student) {
    return String.valueOf(getInitial(student.getFirstName())) +
      getInitial(student.getLastName());
  }

  private static char getInitial(String word) {
    return Character.toUpperCase(word.charAt(0));
  }
}
