package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;

import java.io.*;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This program generates a .mailrc file containing a mail alias for
 * every student in a given grade book.
 */
public class GenerateMailrc {

  /**
   * Generates a mailrc file for the students in a given grade book.
   */
  static void generateMailrc(GradeBook book, PrintWriter mailrc) {
    mailrc.println("# Mail aliases for " + book.getClassName());

    // Sort all of the students by their id
    SortedSet<Student> sorted = new TreeSet<Student>(new Comparator<Student>() {
      public int compare(Student o1, Student o2) {
        String id1 = o1.getId();
        String id2 = o2.getId();
        return id1.compareTo(id2);
      }
    });

    book.forEachStudent(sorted::add);

    Iterator iter = sorted.iterator();
    while (iter.hasNext()) {
      // alias id "email (firstName LastName)"
      Student student = (Student) iter.next();
      String email = student.getEmail();
      if (email != null) {
	mailrc.print("alias " + student.getId() + " \"");
	StringBuffer name = new StringBuffer();

        if (student.getNickName() != null) {
          name.append(student.getNickName());
          name.append(" ");

        } else if (student.getFirstName() != null) {
	  name.append(student.getFirstName());
	  name.append(" ");
	}
	if(student.getLastName() != null) {
	  name.append(student.getLastName());
	}
	if(!name.toString().equals("")) {
	  mailrc.print(name.toString().trim() + " <" + email + ">");
	}
	mailrc.println("\"");
      }
    }
  }

  /**
   * Displays usage information for this program
   */
  private static void usage() {
    PrintStream err = System.err;
    err.println("usage: java GenerateMailrc gradeBookFile mailrcFile");
    System.exit(1);
  }

  /**
   * Main program.
   */
  public static void main(String[] args) {
    if (args.length < 2) {
      System.err.println("** Missing arguments");
      usage();
    }

    String bookName = args[0];
    String mailrcName = args[1];

    GradeBook book = null;
    PrintWriter mailrc = null;
    try {
      XmlGradeBookParser parser = new XmlGradeBookParser(bookName);
      book = parser.parse();
      mailrc = new PrintWriter(new FileWriter(mailrcName), true);

    } catch (FileNotFoundException ex) {
      System.err.println("** Could not find file: " + ex.getMessage());
      System.exit(1);
      
    } catch (IOException ex) {
      System.err.println("** IOException during parsing: " + ex.getMessage());
      System.exit(1);

    } catch (ParserException ex) {
      System.err.println("** Error during parsing: " + ex);
      System.exit(1);
    }

    generateMailrc(book, mailrc);
  }

}
