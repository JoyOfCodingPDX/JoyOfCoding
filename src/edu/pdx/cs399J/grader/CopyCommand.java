package edu.pdx.cs410J.grader;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;

/**
 * This command copies a file from a source to a destination.
 *
 * @author David Whitlock
 */
public class CopyCommand extends Command {

  /** Where we get the file from */
  private String src;

  /** Where the file goes */
  private String dest;

  /**
   * Creates a new <code>CopyCommand</code> that copies a file from a
   * source to a destination.
   */
  public CopyCommand(String src, String dest) {
    this.src = src;
    this.dest = dest;
  }

  /**
   * Copies the file
   */
  void execute(Report report, Properties props) {
    // Copy using streams in case we ever need to copy binary files.
    FileInputStream src = null;
    try {
      src = new FileInputStream(super.expandMacros(this.src, props));

    } catch (FileNotFoundException ex) {
      String s = "Could not find file " + 
        expandMacros(this.src, props);
      report.logException(s, ex);
    }

    try {
      FileOutputStream dest = 
        new FileOutputStream(expandMacros(this.dest, props));
      
      byte[] buffer = new byte[1024];
      int count = src.read(buffer);
      while (count != -1) {
	dest.write(buffer, 0, count);
	count = src.read(buffer);
      }
      src.close();
      dest.close();

    } catch (IOException ex) {
      String s = "While copying " + expandMacros(this.src, props) + 
        " to " + expandMacros(this.dest, props);
      report.logException(s, ex);
    }
  }

  /**
   * Returns a brief textual description of this
   * <code>CopyCommand</code>. 
   */
  public String toString() {
    return "copy " + this.src + " to " + this.dest;
  }

  /**
   * Factory method that creates a new <code>CopyCommand</code> from a
   * portion of an XML DOM tree.
   */
  static CopyCommand fromXML(Element root) {
    if (!root.getTagName().equals("copy")) {
      String s = "Expected <copy>, got <" + root.getTagName() + ">";
      throw new IllegalArgumentException(s);
    }

    String src = null;
    String dest = null;

    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      if (!(node instanceof Element)) {
        continue;
      }

      Element child = (Element) node;
      if (child.getTagName().equals("src")) {
        src = Command.extractTextFrom(child);

      } else if (child.getTagName().equals("dest")) {
        dest = Command.extractTextFrom(child);

      } else {
        String s = "Encountered unknown tag: <" + child.getTagName() + 
          "> while parsing a <copy>";
        throw new IllegalArgumentException(s);
      }
    }

    return new CopyCommand(src, dest);
  }
}
