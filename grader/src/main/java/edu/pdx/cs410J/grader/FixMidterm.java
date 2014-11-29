package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

/**
 * This is a little program that combines the grades for the midterm
 * quiz, the dream job, and the resume into one grade.  This way
 * people who missed this quiz are not penalized unnecessarily.
 */
public class FixMidterm {

  public static void main(String[] args) {
    PrintStream out = System.out;
    PrintStream err = System.err;

    if (args.length < 1) {
      err.println("** usage: java FixMidterm gradebook");
      System.exit(1);
    }

    String xmlFile = args[0];

    GradeBook book;

    File file = new File(xmlFile);
    if (file.exists()) {
      // Parse a grade book from the XML file
      try {
        XmlGradeBookParser parser = new XmlGradeBookParser(file);
        book = parser.parse();

      } catch (FileNotFoundException ex) {
        err.println("** Could not find file: " + ex.getMessage());
        System.exit(1);
        return;

      } catch (IOException ex) {
        err.println("** IOException during parsing: " + ex.getMessage());
        System.exit(1);
        return;

      } catch (ParserException ex) {
        err.println("** Error during parsing: " + ex.getMessage());
        System.exit(1);
        return;
      }

    } else {
      err.println("** File " + file + " does not exist");
      System.exit(1);
      return;
    }

    book.forEachStudent(student -> {
      out.print("Fixing " + student.getId() + ": ");
      out.flush();

      double total = 0.0;

      Grade quiz3 = student.getGrade("quiz3");
      if (quiz3 != null) {
        total += quiz3.getScore();
      } else {
        quiz3 = new Grade("quiz3", 0.0);
      }

      out.print("midterm = " + quiz3.getScore() + ", ");
      out.flush();

      Grade job = student.getGrade("job");
      if (job != null) {
        total += job.getScore();
        quiz3.addNote("Dream job score: " + job.getScore());
        out.print("dream job = " + job.getScore() + ", ");

      } else {
        quiz3.addNote("Missing dream job");
      }

      Grade resume = student.getGrade("resume");
      if (resume != null) {
        total += resume.getScore();
        quiz3.addNote("Resume score: " + resume.getScore());
        out.print("resume = " + resume.getScore() + ", ");
        out.flush();

      } else {
        quiz3.addNote("Missing resume");
      }

      quiz3.setScore(total);
      student.setGrade(quiz3.getAssignmentName(), quiz3);
      out.println("total = " + quiz3.getScore());
    });

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
