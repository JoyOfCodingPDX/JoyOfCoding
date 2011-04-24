package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;
import java.io.*;
import java.util.*;

/**
 * This program imports a bunch of students into a grade book.  It is
 * used at the beginning of the term when students submit their XML
 * files using the {@link Survey} program.
 *
 * @author David Whitlock
 * @version $Revision: 1.2 $
 */
public class ImportStudents {
  private static final PrintStream out = System.out;
  private static final PrintStream err = System.err;

  /**
   * Prints usage information for this program
   */
  private static void usage(String s) {
    err.println("\n** " + s + "\n");
    err.println("usage: java ImportStudents xmlFile (studentXml)+");
    err.println("  xmlFile     The grade book's XML file");
    err.println("  studentXml  A student's XML file");
    err.println("");
    err.println("This program imports student XML files into a ");
    err.println("grade book.");
    err.println("\n");

    System.exit(1);
  }

  public static void main(String[] args) throws Throwable {
    String xmlFileName = null;
    List<String> studentXmlNames = new ArrayList<String>();

    for (int i = 0; i < args.length; i++) {
      if (xmlFileName == null) {
        xmlFileName = args[i];

      } else {
        studentXmlNames.add(args[i]);
      }
    }

    if (xmlFileName == null) {
      usage("Missing grade book XML file");

    } else if (studentXmlNames.isEmpty()) {
      usage("Missing student XML file");
    }

    File xmlFile = new File(xmlFileName);
    if (!xmlFile.exists()) {
      usage("Grade book file \"" + xmlFile + "\" does not exist");
    }

    XmlGradeBookParser parser = new XmlGradeBookParser(xmlFile);
    GradeBook book = parser.parse();
    
    Iterator iter = studentXmlNames.iterator();
    while (iter.hasNext()) {
      File studentFile = new File((String) iter.next());
      if (!studentFile.exists()) {
        err.println("** Student file \"" + studentFile +
                    "\" does not exist");
      }

      XmlStudentParser sp = new XmlStudentParser(studentFile);
      Student student;
      try {
        student = sp.parseStudent();

      } catch (ParserException ex) {
        err.println("** Could not parse student file \"" + studentFile
                    + "\"");
        continue;
      }

      String id = student.getId();
      if (book.containsStudent(id)) {
        err.println("** Grade book already contains \"" + id + "\"");

      } else {
        out.println("Importing " + student);
        book.addStudent(student);
      }
    }

    if (book.isDirty()) {
      XmlDumper dumper = new XmlDumper(xmlFile);
      dumper.dump(book);
    }
  }

}
