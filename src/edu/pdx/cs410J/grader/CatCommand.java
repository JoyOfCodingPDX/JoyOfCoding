package edu.pdx.cs410J.grader;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;

/**
 * This command "cats" (displays) a text file to a <code>Report</code>
 *
 * @author David Whitlock
 */
public class CatCommand extends Command {

  /** The file to cat */
  private String fileName;

  /**
   * Creates a new <code>CatCommand</code> that cats a text file
   */
  public CatCommand(String fileName) {
    this.fileName = fileName;
  }

  /**
   * Prints a text file to a <code>Report</code>
   */
  void execute(Report report, Properties props) {
    String fileName = expandMacros(this.fileName, props);
    File file = new File(fileName);

    report.printlnCentered("File: " + file.getName());
    report.println("");

    if (!file.exists()) {
      report.printlnCentered("Does not exist!");
      return;
    }

    try {
      FileReader fr = new FileReader(file);
      BufferedReader br = new BufferedReader(fr);

      while (br.ready()) {
	String line = br.readLine();
	report.println(line);
      }

    } catch (FileNotFoundException ex) {
      report.logException("Could not find file: " + file, ex);

    } catch (IOException ex) {
      report.logException("While catting file", ex);
    }

    report.println("\n");
  }

  /**
   * Returns a brief textual description of this
   * <code>CatCommand</code>. 
   */
  public String toString() {
    return "cat " + this.fileName;
  }

  /**
   * Factory method that creates a new <code>CatCommand</code> from a
   * portion of an XML DOM tree.
   */
  static CatCommand fromXML(Element root) {
    if (!root.getTagName().equals("cat")) {
      String s = "Expected <cat>, got <" + root.getTagName() + ">";
      throw new IllegalArgumentException(s);
    }

    String fileName = null;
    fileName = Command.extractTextFrom(root);

    if (fileName == null) {
      String s = "No file name for a CatCommand!";
      throw new IllegalArgumentException(s);
    }

    return new CatCommand(fileName);
  }

}
