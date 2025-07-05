package edu.pdx.cs.joy.grader;

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.grader.gradebook.GradeBook;
import edu.pdx.cs.joy.grader.gradebook.Student;
import edu.pdx.cs.joy.grader.gradebook.XmlGradeBookParser;

import java.io.File;
import java.io.IOException;
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

  @VisibleForTesting
  static class StudentInitials implements Comparable<StudentInitials> {
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
      return uppercaseFirstLetter(nameAccessor.get().substring(0, uniqueChars));
    }

    private static String uppercaseFirstLetter(String substring) {
      if (substring.isEmpty()) {
        return "";
      }

      StringBuilder sb = new StringBuilder();
      sb.append(Character.toUpperCase(substring.charAt(0)));
      if (substring.length() > 1) {
        sb.append(substring.substring(1).toLowerCase());
      }
      return sb.toString();
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
      int firstInitialsComparison = this.getFirstNameInitials().compareToIgnoreCase(other.getFirstNameInitials());
      if (firstInitialsComparison != 0) {
        return firstInitialsComparison;
      }

      if (this.uniqueCharsOfLastname > this.uniqueCharsOfFirstname) {
        this.incrementUniqueCharsOfFirstName();
        other.incrementUniqueCharsOfFirstName();

      } else {
        this.incrementUniqueCharsOfLastName();
        other.incrementUniqueCharsOfLastName();
      }
      return this.compareTo(other);
    }

    @VisibleForTesting
    void incrementUniqueCharsOfLastName() {
      if (this.uniqueCharsOfLastname < student.getLastName().length()) {
        this.uniqueCharsOfLastname++;
      }
    }

    @VisibleForTesting
    void incrementUniqueCharsOfFirstName() {
      if (this.uniqueCharsOfFirstname < student.getFirstName().length()) {
        this.uniqueCharsOfFirstname++;
      }
    }
  }

  public static void main(String[] args) {
    if (args.length < 1) {
      usage("Usage: GenerateStudentInitialsFile gradeBookFile");
    }

    String gradeBookFileName = args[0];
    GradeBook book = loadGradeBookFromFile(gradeBookFileName);
    List<String> initials = generateInitials(book.studentsStream());

    for (int i = 0; i < initials.size(); i++) {
      String initial = initials.get(i);
      System.out.format("%2d. %s%n", (i + 1), initial);
    }
  }

  private static GradeBook loadGradeBookFromFile(String gradeBookFileName) {
    File gradeBookFile = getExistingGradeBookFile(gradeBookFileName);
    return parseGradeBookFile(gradeBookFile);
  }

  private static GradeBook parseGradeBookFile(File file) {
    try {
      return new XmlGradeBookParser(file).parse();

    } catch (ParserException | IOException e) {
      usage("While parsing " + file + ": " + e);
      return null;
    }
  }

  private static File getExistingGradeBookFile(String gradeBookFileName) {
    File file = new File(gradeBookFileName);
    if (!file.exists()) {
      usage("Grade book file does not exist: " + gradeBookFileName);
    }
    return file;
  }

  private static void usage(String message) {
    System.err.println(message);
    System.exit(1);
  }
}
