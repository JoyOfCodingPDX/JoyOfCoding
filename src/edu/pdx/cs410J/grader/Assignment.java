package edu.pdx.cs410J.grader;

import java.io.*;
import java.util.*;

import edu.pdx.cs410J.ParserException;

/**
 * This class represents an assignment give to students in CS410J.
 *
 * @author David Whitlock
 */
public class Assignment implements Notable {
  /**
   * The assignment is a project
   */
  public static final int PROJECT = 0;

  /**
   * The assignment is a quiz
   */
  public static final int QUIZ = 1;
  public static final int OTHER = 2;

  private String name;
  private String description;
  private double points;
  private int type;
  private List notes = new ArrayList();
  private boolean dirty;  // Has this assignment been modified?
  
  /**
   * Creates a new <code>Assignment</code> with the given name and
   * point value.
   */
  public Assignment(String name, double points) {
    this.name = name;
    this.points = points;
    this.dirty = false;
  }

  /**
   * Returns the name of this <code>Assignment</code>
   */
  public String getName() {
    return this.name;
  }

  /**
   * Returns the number of points this <code>Assignment</code> is
   * worth
   */
  public double getPoints() {
    return this.points;
  }

  /**
   * Sets the number of points that this <code>Assignment</code> is
   * worth.
   */
  public void setPoints(double points) {
    this.dirty = true;
    this.points = points;
  }

  /**
   * Returns a description of this <code>Assignment</code>
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Sets the description of this <code>Assignment</code>
   */
  public void setDescription(String description) {
    this.dirty = true;
    this.description = description;
  }

  /**
   * Returns the type of this <code>Assignment</code>
   *
   * @see #PROJECT
   * @see #QUIZ
   * @see #OTHER
   */
  public int getType() {
    return this.type;
  }

  /**
   * Sets the type of this <code>Assignment</code>
   *
   * @see #PROJECT
   * @see #QUIZ
   * @see #OTHER
   */
  public void setType(int type) {
    this.dirty = true;
    this.type = type;
  }

  /**
   * Returns notes about this <code>Assignment</code>
   */
  public List getNotes() {
    return this.notes;
  }

  /**
   * Adds a note about this <code>Assignment</code>
   */
  public void addNote(String note) {
    this.dirty = true;
    this.notes.add(note);
  }

  /**
   * Sets the dirtiness of this <code>Assignment</code>
   */
  public void setDirty(boolean dirty) {
    this.dirty = dirty;
  }

  /**
   * Returns <code>true</code> if this <code>Assignment</code> has been
   * modified.
   */
  public boolean isDirty() {
    return this.dirty;
  }

  /**
   * Marks this <code>Assignment</code> as being clean
   */
  public void makeClean() {
    this.setDirty(false);
  }

  /**
   * Returns a brief textual description of this
   * <code>Assignment</code>
   */
  public String toString() {
    return "Assignment " + this.getName() + " worth " +
           this.getPoints();
  }
  

  private static PrintWriter out = new PrintWriter(System.out, true);
  private static PrintWriter err = new PrintWriter(System.err, true);

  /**
   * Prints usage information about the main program.
   */
  private static void usage() {
    err.println("\njava Assignment [options] -xmlFile xmlFile " +
                "-name name");
    err.println("  where [options] are:");
    err.println("  -points points        Points assignment is worth");
    err.println("  -description desc     Description of assignment");
    err.println("  -type type            Type of assignment " +
                "(PROJECT, QUIZ, or OTHER)");
    err.println("  -note note            Note about assignment");
    err.println("\n");
    System.exit(1);
  }

  /**
   * Main program that creates/edits an <code>Assignment</code> in a
   * grade book stored in an XML file.
   */
  public static void main(String[] args) {
    String fileName = null;
    String name = null;
    String points = null;
    String desc = null;
    Integer type = null;
    String note = null;

    // Parse the command line
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-xmlFile")) {
        if (++i >= args.length) {
          err.println("** Missing XML file name");
          usage();
        }

        fileName = args[i];

      } else if (args[i].equals("-name")) {
        if (++i >= args.length) {
          err.println("** Missing assignment name");
          usage();
        }

        name = args[i];

      } else if (args[i].equals("-points")) {
        if (++i >= args.length) {
          err.println("** Missing points value");
          usage();
        }

        points = args[i];

      } else if (args[i].equals("-description")) {
        if (++i >= args.length) {
          err.println("** Missing description");
          usage();
        }
        
        desc = args[i];

      } else if (args[i].equals("-type")) {
        if (++i >= args.length) {
          err.println("** Missing type");
          usage();
        }

        // Make sure type is valid
        if (args[i].equals("PROJECT")) {
          type = new Integer(Assignment.PROJECT);

        } else if (args[i].equals("QUIZ")) {
          type = new Integer(Assignment.QUIZ);

        } else if (args[i].equals("OTHER")) {
          type = new Integer(Assignment.OTHER);

        } else {
          err.println("** Invalid type: " + args[i]);
          usage();
        }

      } else if (args[i].equals("-note")) {
        if (++i >= args.length) {
          err.println("** Missing note");
          usage();
        }

        note = args[i];

      } else {
        err.println("** Unknown option: " + args[i]);
      }
    }

    // Verify command line
    if (fileName == null) {
      err.println("** No file name specified");
      usage();
    }

    if (name == null) {
      err.println("** No assignment name specified");
      usage();
    }

    File file = new File(fileName);
    if (!file.exists()) {
      err.println("** Grade book file " + fileName + 
                  " does not exist");
      System.exit(1);
    }

    if (name == null) {
      err.println("** No assignment name specified");
      usage();
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

    // Get the assignment
    Assignment assign = book.getAssignment(name);
    if (assign == null) {
      // Did we specify a points value?
      if (points == null) {
        err.println("** No points specified");
        usage();
      }

      double value = -1.0;
      try {
        value = Double.parseDouble(points);

      } catch (NumberFormatException ex) {
        err.println("** Not a valid point value: " + points);
        System.exit(1);
      }

      if (value < 0.0) {
        err.println("** Not a valid point value: " + value);
        System.exit(1);
      }

      // Create a new Assignment
      assign = new Assignment(name, value);
      book.addAssignment(assign);
    }

    // Set the properties of the assignment
    if (desc != null) {
      assign.setDescription(desc);
    }

    if (type != null) {
      assign.setType(type.intValue());
    }

    if (note != null) {
      assign.addNote(note);
    }

    // Write the grade book back out to the XML file
    try {
      XmlDumper dumper = new XmlDumper(file);
      dumper.dump(book);

    } catch (IOException ex) {
      err.println("** While dumping to " + file + ": " + ex);
      System.exit(1);
    }
  }
}
