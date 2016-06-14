package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.grader.Student.Section;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static edu.pdx.cs410J.grader.GradeBook.LetterGradeRanges.LetterGradeRange;

/**
 * This class represents a grade book that contains information about
 * a CS410J class: the assignments, the students and their grades.
 *
 * @author David Whitlock
 * @since Fall 2000
 */
public class GradeBook {

  /** The name of the class */
  private String className;

  /** Maps the name of an assignment to an <code>Assignment</code> */
  private Map<String, Assignment> assignments = new TreeMap<>();

  /** Maps the id of a student to a <code>Student</code> object */
  private Map<String, Student> students = new TreeMap<>();

  /** Has the grade book been modified? */
  private boolean dirty = true;

  /** The Course Request Number (CRN) for this gradebook */
  private int crn = 0;

  private final Map<Section, LetterGradeRanges> letterGradeRanges = new HashMap<>();
  
  /**
   * Creates a new <code>GradeBook</code> for a given class
   */
  public GradeBook(String className) {
    this.className = className;
    for(Section section : getSections()) {
      letterGradeRanges.put(section, new LetterGradeRanges());
    }
  }

  public Iterable<Section> getSections() {
    return Arrays.asList(Section.UNDERGRADUATE, Section.GRADUATE);
  }

  /**
   * Returns the name of the class represented by this
   * <code>GradeBook</code>
   */
  public String getClassName() {
    return this.className;
  }

  /**
   * Returns the names of the assignments for this class
   */
  public Set<String> getAssignmentNames() {
    return this.assignments.keySet();
  }

  /**
   * Sets the Course Request Number (CRN) for this grade book.
   *
   * @since Spring 2005
   */
  public void setCRN(int crn) {
    this.setDirty(true);
    this.crn = crn;
  }

  /**
   * Returns the Course Request Number (CRN) for this grade book.
   *
   * @since Spring 2005
   */
  public int getCRN() {
    return this.crn;
  }

  /**
   * Returns the <code>Assignment</code> of a given name
   */
  public Assignment getAssignment(String name) {
    return this.assignments.get(name);
  }

  /**
   * Adds an <code>Assignment</code> to this class
   */
  public void addAssignment(Assignment assign) {
    this.setDirty(true);
    this.assignments.put(assign.getName(), assign);
  }

  /**
   * Returns the ids of all of the students in this class
   */
  public Set<String> getStudentIds() {
    return this.students.keySet();
  }

  public Optional<Student> getStudent(String id) {
    return Optional.ofNullable(this.students.get(id));
  }

  public Stream<Student> studentsStream() {
    return this.students.values().stream();
  }

  /**
   * Adds a <code>Student</code> to this <code>GradeBook</code>
   *
   * @see #containsStudent
   */
  public void addStudent(Student student) {
    this.setDirty(true);
    this.students.put(student.getId(), student);
  }

  /**
   * Removes a <code>Student</code> from this <code>GradeBook</code>
   */
  public void removeStudent(Student student) {
    if (this.students.remove(student.getId()) != null) {
      this.setDirty(true);
    }
  }

  /**
   * Returns whether or not this grade book contains a student with
   * the given id.
   */
  public boolean containsStudent(String id) {
    return this.students.containsKey(id);
  }

  /**
   * Sets the dirtiness of this <code>GradeBook</code>
   */
  public void setDirty(boolean dirty) {
    this.dirty = dirty;
  }

  /**
   * Marks this <code>GradeBook</code> as being clean
   */
  public void makeClean() {
    this.setDirty(false);

    for (Assignment assignment : this.assignments.values()) {
      assignment.makeClean();
    }

    for (Student student : this.students.values()) {
      student.makeClean();
    }
  }

  /**
   * Returns <code>true</code> if this <code>GradeBook</code> has been
   * modified.
   */
  public boolean isDirty() {
    if (this.dirty) {
      return true;
    }

    // Are any of the Assignments dirty?
    Iterator iter = this.assignments.values().iterator();
    while (iter.hasNext()) {
      Assignment assign = (Assignment) iter.next();
      if (assign.isDirty()) {
        return true;
      }
    }

    // Are any of the Students dirty?
    iter = this.students.values().iterator();
    while (iter.hasNext()) {
      Student student = (Student) iter.next();
      if (student.isDirty()) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns a brief textual description of this
   * <code>GradeBook</code>.
   */
  public String toString() {
    return "Grade book for " + this.getClassName() + " with " +
           this.getStudentIds().size() + " students";
  }

  private static PrintWriter err = new PrintWriter(System.err, true);

  /**
   * Prints usage information about the main program.
   */
  private static void usage() {
    err.println("\nusage: java GradeBook -file xmlFile [options]");
    err.println("Where [options] are:");
    err.println("  -name className      Create new class file");
    err.println("  -import xmlName      Import a student from a file");
    err.println("\n");
    System.exit(1);
  }

  /**
   * Main program that is used to create a <code>GradeBook</code>
   */
  public static void main(String[] args) {
    String xmlFile = null;
    String name = null;
    String importName = null;

    // Parse the command line
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-file")) {
        if (++i >= args.length) {
          err.println("** Missing file name");
          usage();
        }

        xmlFile = args[i];

      } else if (args[i].equals("-name")) {
        if (++i >= args.length) {
          err.println("** Missing class name");
          usage();
        }

        name = args[i];

      } else if (args[i].equals("-import")) {
        if (++i >= args.length) {
          err.println("** Missing import file name");
          usage();
        }

        importName = args[i];

      } else {
        err.println("** Spurious command line option: " + args[i]);
        usage();
      }
    }

    if (xmlFile == null) {
      err.println("** No XML file specified");
      usage();
    }

    GradeBook book = null;

    File file = new File(xmlFile);
    if (file.exists()) {
      // Parse a grade book from the XML file
      try {
        XmlGradeBookParser parser = new XmlGradeBookParser(file);
        book = parser.parse();

      } catch (FileNotFoundException ex) {
        err.println("** Could not find file: " + ex.getMessage());
        System.exit(1);

      } catch (IOException ex) {
        err.println("** IOException during parsing: " + ex.getMessage());
        System.exit(1);

      } catch (ParserException ex) {
        err.println("** Error during parsing: " + ex.getMessage());
        System.exit(1);
      }

      // Do we import?
      if (importName != null) {
        File importFile = new File(importName);
        if (!importFile.exists()) {
          err.println("** Import file " + importFile.getName() + 
                      " does not exist");
          System.exit(1);
        }

        try {
          XmlStudentParser sp = new XmlStudentParser(importFile);
          Student student = sp.parseStudent();
          book.addStudent(student);

        } catch (IOException ex) {
          err.println("** Error during parsing: " + ex.getMessage());
          System.exit(1);

        } catch (ParserException ex) {
          err.println("** Error during parsing: " + ex.getMessage());
          System.exit(1);
        }
      }

    } else if (name == null) {
      err.println("** Must specify a class name when creating a " +
                  "grade book");
      System.exit(1);
      
    } else {
      // Create an empty GradeBook
      book = new GradeBook(name);
    }

    // Write the grade book to the XML file
    try {
      XmlDumper dumper = new XmlDumper(file);
      dumper.dump(book);

    } catch (IOException ex) {
      err.println("** Error while writing XML file: " + ex);
      System.exit(1);
    }
  }

  public LetterGradeRanges getLetterGradeRanges(Section section) {
    return letterGradeRanges.get(section);
  }

  public LetterGrade getLetterGradeForScore(Section section, double score) {
    for (LetterGradeRange range : this.getLetterGradeRanges(section)) {
      if (range.isScoreInRange(score)) {
        return range.letterGrade();
      }
    }

    throw new IllegalStateException("Could not find a letter grade range for " + score);
  }

  public void forEachStudent(Consumer<Student> consumer) {
    this.students.values().forEach(consumer);
  }

  public Stream<Assignment> assignmentsStream() {
    return this.assignments.values().stream();
  }

  public Optional<Student> getStudentWithSsn(String ssn) {
    return this.studentsStream().filter(s -> ssn.equals(s.getSsn())).findAny();
  }

  static class LetterGradeRanges implements Iterable<LetterGradeRanges.LetterGradeRange> {
    private final Map<LetterGrade, LetterGradeRange> ranges = new TreeMap<>();

    private LetterGradeRanges() {
      createDefaultLetterGradeRange(LetterGrade.A, 94, 100);
      createDefaultLetterGradeRange(LetterGrade.A_MINUS, 90, 93);
      createDefaultLetterGradeRange(LetterGrade.B_PLUS, 87, 89);
      createDefaultLetterGradeRange(LetterGrade.B, 83, 86);
      createDefaultLetterGradeRange(LetterGrade.B_MINUS, 80, 82);
      createDefaultLetterGradeRange(LetterGrade.C_PLUS, 77, 79);
      createDefaultLetterGradeRange(LetterGrade.C, 73, 76);
      createDefaultLetterGradeRange(LetterGrade.C_MINUS, 70, 72);
      createDefaultLetterGradeRange(LetterGrade.D_PLUS, 67, 69);
      createDefaultLetterGradeRange(LetterGrade.D, 63, 66);
      createDefaultLetterGradeRange(LetterGrade.D_MINUS, 60, 62);
      createDefaultLetterGradeRange(LetterGrade.F, 0, 59);
    }

    private void createDefaultLetterGradeRange(LetterGrade letterGrade, int minimum, int maximum) {
      this.ranges.put(letterGrade, new LetterGradeRange(letterGrade, minimum, maximum));
    }

    public LetterGradeRange getRange(LetterGrade letterGrade) {
      return ranges.get(letterGrade);
    }

    public void validate() {
      validateThatFRangeHasAMinimumOf0();
      validateThatARangeContains100();
      validateThatRangesAreContiguous();
    }

    private void validateThatRangesAreContiguous() {
      LetterGradeRange previous = null;

      for (LetterGrade letterGrade : ranges.keySet()) {
        LetterGradeRange current = ranges.get(letterGrade);
        if (previous != null) {
          if (previous.minimum() != current.maximum() + 1) {
            String s = "There is a gap between the range for " + previous + " and " + current;
            throw new LetterGradeRange.InvalidLetterGradeRange(s);
          }
        }

        previous = current;
      }

    }

    private void validateThatARangeContains100() {
      LetterGradeRange range = getRange(LetterGrade.A);
      if (range.maximum() < 100) {
        throw new LetterGradeRange.InvalidLetterGradeRange("The A range must contain 100");
      }
    }

    private void validateThatFRangeHasAMinimumOf0() {
      LetterGradeRange range = getRange(LetterGrade.F);
      if (range.minimum() > 0) {
        throw new LetterGradeRange.InvalidLetterGradeRange("The F range must contain zero");
      }
    }

    @Override
    public Iterator<LetterGradeRange> iterator() {
      return this.ranges.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super LetterGradeRange> action) {
      this.ranges.values().forEach(action);
    }

    @Override
    public Spliterator<LetterGradeRange> spliterator() {
      return this.ranges.values().spliterator();
    }

    static class LetterGradeRange {
      private final LetterGrade letterGrade;
      private int maximum;
      private int minimum;

      public LetterGradeRange(LetterGrade letterGrade, int minimum, int maximum) {
        this.letterGrade = letterGrade;
        setRange(minimum, maximum);
      }

      public void setRange(int minimum, int maximum) {
        if (minimum >= maximum) {
          String s = String.format("Minimum value (%d) must be less than maximum value (%d)",
            minimum, maximum);
          throw new InvalidLetterGradeRange(s);
        }

        this.minimum = minimum;
        this.maximum = maximum;
      }

      public int minimum() {
        return this.minimum;
      }

      public int maximum() {
        return this.maximum;
      }

      @Override
      public String toString() {
        return "Range for " + letterGrade() + " is " + minimum() + " to " + maximum();
      }

      public LetterGrade letterGrade() {
        return this.letterGrade;
      }

      public boolean isScoreInRange(double score) {
        int intScore = (int) Math.round(score);
        return intScore >= minimum() && intScore <= maximum();
      }

      public static class InvalidLetterGradeRange extends RuntimeException {
        public InvalidLetterGradeRange(String message) {
          super(message);
        }
      }
    }
  }
}
