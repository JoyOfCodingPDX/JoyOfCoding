package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * This class represents a student who is taking CS410J.
 */
public class Student extends NotableImpl {

  private String id;
  private String firstName;
  private String lastName;
  private String nickName;
  private String email;
  private String ssn;
  private String major;
  private LetterGrade letterGrade;

  /** Maps name of Assignment to Grade.  The grades are sorted so that
   * they will appear in a canonical order in the student's XML
   * file. */
  private SortedMap<String, Grade> grades;

  private List<String> late;   // Names of late Assignments
  private List<String> resubmitted;  // Names of resubmitted Assignments
  private String d2LId;
  private Section enrolledSection;

  ///////////////////////  Constructors  ///////////////////////

  /**
   * Creates a new <code>Student</code> with a given id.  An example
   * of an id is the student's UNIX login name.
   */
  public Student(String id) {
    this.id = id;
    this.grades = new TreeMap<>();
    this.late = new ArrayList<>();
    this.resubmitted = new ArrayList<>();
  }

  ///////////////////////  Instance Methods  ///////////////////////

  /**
   * Returns the id of this <code>Student</code>
   */
  public String getId() {
    return this.id;
  }

  /**
   * Returns the first name of this <code>Student</code>
   */
  public String getFirstName() {
    return this.firstName;
  }

  /**
   * Returns the last name of this <code>Student</code>
   */
  public Student setFirstName(String firstName) {
    this.setDirty(true);
    this.firstName = firstName;
    return this;
  }

  /**
   * Returns the last name of this <code>Student</code>
   */
  public String getLastName() {
    return this.lastName;
  }

  /**
   * Returns the last name of this <code>Student</code>
   */
  public Student setLastName(String lastName) {
    this.setDirty(true);
    this.lastName = lastName;
    return this;
  }

  /**
   * Returns the nick name of this <code>Student</code>
   */
  public String getNickName() {
    return this.nickName;
  }

  /**
   * Returns the nick name of this <code>Student</code>
   */
  public void setNickName(String nickName) {
    this.setDirty(true);
    this.nickName = nickName;
  }

  /**
   * Returns this <code>Student</code>'s full name including first,
   * last, and nick names.
   */
  public String getFullName() {
    StringBuilder sb = new StringBuilder();
    if (this.firstName != null) {
      sb.append(this.firstName);
    }

    if (this.nickName != null) {
      sb.append(" \"").append(this.nickName).append("\"");
    }

    if (this.lastName != null) {
      sb.append(" ").append(this.lastName);
    }

    return sb.toString().trim();
  }

  /**
   * Returns the email address of this <code>Student</code>
   */
  public String getEmail() {
    return this.email;
  }

  /**
   * Sets the email address of this <code>Student</code>
   */
  public Student setEmail(String email) {
    this.setDirty(true);
    this.email = email;
    return this;
  }

  /**
   * Returns the social security number of this <code>Student</code>
   */
  public String getSsn() {
    return this.ssn;
  }

  /**
   * Sets the social security number of this <code>Student</code>
   */
  public Student setSsn(String ssn) {
    this.setDirty(true);
    this.ssn = ssn;
    return this;
  }

  /**
   * Returns the major of this <code>Student</code>
   */
  public String getMajor() {
    return this.major;
  }

  /**
   * Sets the major of this <code>Student</code>
   */
  public void setMajor(String major) {
    this.setDirty(true);
    this.major = major;
  }

  /**
   * Returns the names of the assignments for which this
   * <code>Student</code> has received a <code>Grade</code>.
   */
  public Set<String> getGradeNames() {
    return this.grades.keySet();
  }

  /**
   * Returns the <code>Grade</code> a student received on an
   * assignment of a given name.  If the student has no grade for that
   * assignment, <code>null</code> is returned.
   */
  public Grade getGrade(String assignmentName) {
    return this.grades.get(assignmentName);
  }

  /**
   * Sets a <code>Grade</code> a student received on an assignment of
   * a given name.
   */
  public void setGrade(String assignmentName, Grade grade) {
    this.setDirty(true);
    this.grades.put(assignmentName, grade);
  }

  /**
   * Returns the names of all of the assignments that are late.
   */
  public List<String> getLate() {
    return this.late;
  }

  /**
   * Makes note of the name of an assignment that is late
   */
  public void addLate(String assignmentName) {
    this.setDirty(true);
    this.late.add(assignmentName);
  }

  /**
   * Returns the names of all of the assignments that are resubmitted.
   */
  public List<String> getResubmitted() {
    return this.resubmitted;
  }

  /**
   * Makes note of the name of an assignment that is resubmitted
   */
  public void addResubmitted(String assignmentName) {
    this.setDirty(true);
    this.resubmitted.add(assignmentName);
  }
  
  /**
   * Marks this <code>Student</code> as being clean
   */
  @Override
  public void makeClean() {
	  super.makeClean();

    // Make all Grades clean
    this.grades.values().forEach(edu.pdx.cs410J.grader.Grade::makeClean);
  }

  /**
   * If any of its grades is dirty, then the student is dirty
   */
  @Override
  public boolean isDirty() {
    if (super.isDirty()) {
      return true;
      
    } else {
      for (Grade grade : this.grades.values()) {
        if (grade.isDirty()) {
          return true;
        }
      }
      
      return false;
    }
  }

  /**
   * Returns a complete textual description of this
   * <code>Student</code>.
   */
  String getDescription() {
    Student student = this;

    StringBuilder sb = new StringBuilder();
    sb.append(student.getId()).append(": ");
    sb.append(student.getFullName());

    String email = student.getEmail();
    if (email != null && !email.equals("")) {
      sb.append(", ").append(email);
    }

    String ssn = student.getSsn();
    if (ssn != null && !ssn.equals("")) {
      sb.append(", ").append(ssn);
    }

    String major = student.getMajor();
    if (major != null && !major.equals("")) {
      sb.append(", ").append(major);
    }

    for (Object note : this.getNotes()) {
      sb.append(", \"").append(note).append("\"");
    }

    return sb.toString();
  }

  ///////////////////////  Utility Methods  ///////////////////////

  /**
   * Two <code>Student</code>s are equal if they have the same id
   */
  public boolean equals(Object o) {
    return o instanceof Student && this.getId().equals(((Student) o).getId());
  }

  /**
   * Two students that are equal must have the same hash code
   */
  public int hashCode() {
    return this.getId().hashCode();
  }

  /**
   * Returns a brief textual description of this <code>Student</code>
   */
  public String toString() {
    return this.getId() + " (" + this.getFullName() + ")";
  }

  ///////////////////////  Main Program  ///////////////////////

  private static PrintWriter err = new PrintWriter(System.err, true);

  /**
   * Prints usage information about the main program
   */
  private static void usage() {
    err.println("\nusage: java Student -id id -file xmlFile [options]");
    err.println("  where [options] are:");
    err.println("  -firstName firstName    Student's first name");
    err.println("  -lastName lastName      Student's last name");
    err.println("  -nickName nickName      Student's nick name");
    err.println("  -email email            Student's email address");
    err.println("  -ssn SSN                Student's social security"
                + " number");
    err.println("  -major major            Student's major");
    err.println("  -note note              A note about the student");
    err.println("\n");
    System.exit(1);
  }

  /**
   * Main program that is used to add a <code>Student</code> to a
   * grade book.
   */
  public static void main(String[] args) {
    String id = null;
    String xmlFile = null;
    String firstName = null;
    String lastName = null;
    String nickName = null;
    String email = null;
    String ssn = null;
    String major = null;
    String note = null;

    // Parse the command line
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-id")) {
        if (++i >= args.length) {
          err.println("** Missing id");
          usage();
        }

        id = args[i];

      } else if (args[i].equals("-xmlFile") || args[i].equals("-file")) {
        if (++i >= args.length) {
          err.println("** Missing xml file name");
          usage();
        }

        xmlFile = args[i];

      } else if (args[i].equals("-firstName")) {
        if (++i >= args.length) {
          err.println("** Missing first name");
          usage();
        }

        firstName = args[i];

      } else if (args[i].equals("-lastName")) {
        if (++i >= args.length) {
          err.println("** Missing last name");
          usage();
        }

        lastName = args[i];

      } else if (args[i].equals("-nickName")) {
        if (++i >= args.length) {
          err.println("** Missing nick name");
          usage();
        }

        nickName = args[i];

      } else if (args[i].equals("-email")) {
        if (++i >= args.length) {
          err.println("** Missing email address");
          usage();
        }

        email = args[i];

      } else if (args[i].equals("-ssn")) {
        if (++i >= args.length) {
          err.println("** Missing social security number");
          usage();
        }

        ssn = args[i];

      } else if (args[i].equals("-major")) {
        if (++i >= args.length) {
          err.println("** Missing major");
          usage();
        }

        major = args[i];

      } else if (args[i].equals("-note")) {
        if (++i >= args.length) {
          err.println("** Missing text of note");
          usage();
        }

        note = args[i];

      } else if (args[i].startsWith("-")) {
        err.println("** Unknown option: " + args[i]);
        usage();

      } else {
        err.println("** Spurious command line: " + args[i]);
        usage();
      }
    }

    // Check to make sure that command line was entered correctly
    if (id == null) {
      err.println("** No id specified");
      usage();
    }

    if (xmlFile == null) {
      err.println("** No XML file specified");
      usage();
    }

    // Parse the XML file to get a GradeBook
    File file = new File(xmlFile);
    if (!file.exists()) {
      err.println("** Grade book file " + xmlFile + 
                  " does not exist");
      System.exit(1);
    }

    GradeBook book = null;
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
      err.println("** Exception while parsing " + file + ": " + ex);
      System.exit(1);
    }

    // Get the Student
    Student student = book.getStudent(id).get();

    if (firstName != null) {
      student.setFirstName(firstName);
    }

    if (lastName != null) {
      student.setLastName(lastName);
    }

    if (nickName != null) {
      student.setNickName(nickName);
    }

    if (email != null) {
      student.setEmail(email);
    }

    if (ssn != null) {
      student.setSsn(ssn);
    }

    if (major != null) {
      student.setMajor(major);
    }

    if (note != null) {
      student.addNote(note);
    }

    // Write the changes back out to the XML file
    try {
      XmlDumper dumper = new XmlDumper(file);
      dumper.dump(book);

    } catch (IOException ex) {
      err.println("** While dumping to " + file + ": " + ex);
      System.exit(1);
    }
  }

  public Grade getGrade(Assignment project) {
    return this.getGrade(project.getName());
  }

  public void setD2LId(String d2LId) {
    this.setDirty(true);
    this.d2LId = d2LId;
  }

  public String getD2LId() {
    return d2LId;
  }

  public LetterGrade getLetterGrade() {
    return letterGrade;
  }

  public void setLetterGrade(LetterGrade letterGrade) {
    this.setDirty(true);
    this.letterGrade = letterGrade;
  }

  public Student setGrade(Assignment assignment, Grade grade) {
    setGrade(assignment.getName(), grade);
    return this;
  }

  public void addLate(Assignment assignment) {
    this.addLate(assignment.getName());
  }

  public void setGrade(Assignment assignment, double score) {
    this.setGrade(assignment, new Grade(assignment, score));

  }

  public void setEnrolledSection(Section enrolledSection) {
    this.enrolledSection = enrolledSection;
  }

  public Section getEnrolledSection() {
    return enrolledSection;
  }

  public enum Section {
    UNDERGRADUATE("undergraduate"), GRADUATE("graduate");

    private final String stringValue;

    Section(String stringValue) {
      this.stringValue = stringValue;
    }

    public static Section fromString(String string) {
      for (Section section : values()) {
        if (section.asString().equals(string)) {
          return section;
        }
      }

      throw new IllegalArgumentException("Could not find LetterGrade for string \"" + string + "\"");
    }

    public String asString() {
      return stringValue;
    }

    public String toString() {
      return asString();
    }

  }
}
