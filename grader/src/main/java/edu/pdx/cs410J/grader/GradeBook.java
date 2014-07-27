package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
  private Map<String, Assignment> assignments;

  /** Maps the id of a student to a <code>Student</code> object */
  private Map<String, Student> students;

  /** Has the grade book been modified? */
  private boolean dirty;

  /** The Course Request Number (CRN) for this gradebook */
  private int crn;
  
  /**
   * Creates a new <code>GradeBook</code> for a given class
   */
  public GradeBook(String className) {
    this.className = className;
    this.assignments = new TreeMap<String, Assignment>();
    this.students = new TreeMap<String, Student>();
    this.dirty = true;
    this.crn = 0;
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
    return (Assignment) this.assignments.get(name);
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

  /**
   * Returns the <code>Student</code> with the given id.  If a student
   * with that name does not exist, a new <code>Student</code> is
   * created.
   */
  public Student getStudent(String id) {
    Student student = (Student) this.students.get(id);
    if (student == null) {
      student = new Student(id);
      this.addStudent(student);
    }
    return student;
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

}
