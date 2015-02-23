package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

/**
 * This class represents an assignment give to students in CS410J.
 *
 * @author David Whitlock
 */
public class Assignment extends NotableImpl {

  public enum AssignmentType {
    PROJECT,
    QUIZ,
    OTHER,
    POA,
    OPTIONAL
  }

  private String name;
  private String description;
  private double points;
  private AssignmentType type;
  private LocalDateTime dueDate;

  /**
   * Creates a new <code>Assignment</code> with the given name and
   * point value.
   */
  public Assignment(String name, double points) {
    this.name = name;
    this.points = points;
    this.type = AssignmentType.PROJECT;
    this.setDirty(false);
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
  public Assignment setPoints(double points) {
    this.setDirty(true);
    this.points = points;
    return this;
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
    this.setDirty(true);
    this.description = description;
  }

  /**
   * Returns the type of this <code>Assignment</code>
   */
  public AssignmentType getType() {
    return this.type;
  }

  /**
   * Sets the type of this <code>Assignment</code>
   */
  public Assignment setType(AssignmentType type) {
    this.setDirty(true);
    this.type = type;
    return this;
  }

  /**
   * Returns a brief textual description of this
   * <code>Assignment</code>
   */
  public String toString() {
    return "Assignment " + this.getName() + " worth " +
           this.getPoints();
  }
  

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
    AssignmentType type = null;
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
        switch (args[i]) {
          case "PROJECT":
            type = AssignmentType.PROJECT;
            break;
          case "QUIZ":
            type = AssignmentType.QUIZ;
            break;
          case "POA":
            type = AssignmentType.POA;
            break;
          case "OTHER":
            type = AssignmentType.OTHER;
            break;
          case "OPTIONAL":
            type = AssignmentType.OPTIONAL;
            break;
          default:
            err.println("** Invalid type: " + args[i]);
            usage();
            break;
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
      assign.setType(type);
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

  public void setDueDate(LocalDateTime dueDate) {
    this.dueDate = dueDate;
  }

  public LocalDateTime getDueDate() {
    return dueDate;
  }

  public boolean isSubmissionLate(LocalDateTime submissionTime) {
    if (this.dueDate == null) {
      return false;

    } else {
      return submissionTime.isAfter(this.dueDate);
    }
  }
}
