package edu.pdx.cs399J.grader;

import java.io.*;
import java.util.*;

import edu.pdx.cs399J.ParserException;

/**
 * This class represents a student who is taking CS399J.
 */
public class Student implements Notable {

  private String id;
  private String firstName;
  private String lastName;
  private String nickName;
  private String email;
  private String ssn;
  private String major;

  /** Maps name of Assignment to Grade.  The grades are sorted so that
   * they will appear in a canonical order in the student's XML
   * file. */
  private SortedMap grades;

  private List late;   // Names of late Assignments
  private List resubmitted;  // Names of resubmitted Assignments

  private List notes;  // Notes about the student

  private boolean dirty;

  ///////////////////////  Constructors  ///////////////////////

  /**
   * Creates a new <code>Student</code> with a given id.  An example
   * of an id is the student's UNIX login name.
   */
  public Student(String id) {
    this.id = id;
    this.grades = new TreeMap();
    this.late = new ArrayList();
    this.resubmitted = new ArrayList();
    this.notes = new ArrayList();
    this.setDirty(true);
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
  public void setFirstName(String firstName) {
    this.setDirty(true);
    this.firstName = firstName;
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
  public void setLastName(String lastName) {
    this.setDirty(true);
    this.lastName = lastName;
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
    StringBuffer sb = new StringBuffer();
    if (this.firstName != null) {
      sb.append(this.firstName);
    }

    if (this.nickName != null) {
      sb.append(" \"" + this.nickName + "\"");
    }

    if (this.lastName != null) {
      sb.append(" " + this.lastName);
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
  public void setEmail(String email) {
    this.setDirty(true);
    this.email = email;
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
  public void setSsn(String ssn) {
    this.setDirty(true);
    this.ssn = ssn;
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
  public Set getGradeNames() {
    return this.grades.keySet();
  }

  /**
   * Returns the <code>Grade</code> a student received on an
   * assignment of a given name.  If the student has no grade for that
   * assignment, <code>null</code> is returned.
   */
  public Grade getGrade(String assignmentName) {
    return (Grade) this.grades.get(assignmentName);
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
  public List getLate() {
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
  public List getResubmitted() {
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
   * Returns all of the notes about this <code>Student</code>
   */
  public List getNotes() {
    return this.notes;
  }

  /**
   * Adds a note about this <code>Student</code>
   */
  public void addNote(String note) {
    this.setDirty(true);
    this.notes.add(note);
  }

  public void removeNote(String note) {
    this.setDirty(true);
    this.notes.remove(note);
  }

  /**
   * Returns the "dirtiness" of this <code>Student</code>.  That is,
   * has the <code>Student</code> been modified.
   */
  boolean isDirty() {
    Iterator iter = this.grades.values().iterator();
    while (iter.hasNext()) {
      Grade grade = (Grade) iter.next();
      if (grade.isDirty()) {
        return true;
      }
    }
    return this.dirty;
  }

  /**
   * Sets the "dirtiness" of this <code>Student</code>.  That is, has
   * the <code>Student</code> been modified.
   */
  void setDirty(boolean dirty) {
    this.dirty = dirty;
  }
  
  /**
   * Marks this <code>Student</code> as being clean
   */
  public void makeClean() {
    this.setDirty(false);

    // Make all Grades clean
    Iterator iter = this.grades.values().iterator();
    while (iter.hasNext()) {
      Grade grade = (Grade) iter.next();
      grade.makeClean();
    }
  }

  /**
   * Returns a complete textual description of this
   * <code>Student</code>.
   */
  String getDescription() {
    Student student = this;

    StringBuffer sb = new StringBuffer();
    sb.append(student.getId() + ": ");
    sb.append(student.getFullName());

    String email = student.getEmail();
    if (email != null && !email.equals("")) {
      sb.append(", " + email);
    }

    String ssn = student.getSsn();
    if (ssn != null && !ssn.equals("")) {
      sb.append(", " + ssn);
    }

    String major = student.getMajor();
    if (major != null && !major.equals("")) {
      sb.append(", " + major);
    }

    Iterator iter = this.getNotes().iterator();
    while (iter.hasNext()) {
      String note = (String) iter.next();
      sb.append(", \"" + note + "\"");
    }

    return sb.toString();
  }

  ///////////////////////  Utility Methods  ///////////////////////

  /**
   * Two <code>Student</code>s are equal if they have the same id
   */
  public boolean equals(Object o) {
    if (o instanceof Student) {
      return this.getId().equals(((Student) o).getId());

    } else {
      return false;
    }
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

  private static PrintWriter out = new PrintWriter(System.out, true);
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
    Student student = book.getStudent(id);

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
}
