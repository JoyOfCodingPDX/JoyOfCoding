package edu.pdx.cs410J.grader;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;

/**
 * A <code>Test</code> is a composite command that consists of other
 * <code>Command</code>s.
 *
 * @author David Whitlock
 */
public class Test extends Command {

  /** The name of this test */
  private String name;

  /** A description of this test */
  private String description;

  /** The commands to be executed */
  private List commands;

  /**
   * Creates a new <code>Test</code> with a given name and description
   */
  public Test(String name, String description) {
    this.name = name;
    this.description = description;
    this.commands = new ArrayList();
  }

  /**
   * Adds a command to be executed
   */
  void addCommand(Command command) {
    this.commands.add(command);
  }

  /**
   * Sets the <code>ClassLoader</code> for all of this tests' commands
   */
  void setClassLoader(ClassLoader loader) {
    for (int i = 0; i < this.commands.size(); i++) {
      ((Command) this.commands.get(i)).setClassLoader(loader);
    }
  }

  /**
   * Executes all of the commands in this test
   */
  void execute(Report report, Properties props) {
    report.printBanner(this.name + ": " + this.description, '*');

    for (int i = 0; i < this.commands.size(); i++) {
      ((Command) this.commands.get(i)).execute(report, props);
    }
  }

  /**
   * Returns a brief textual description of this <code>Test</code>
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("Test " + this.name + ": " + this.description + "\n");
    for (int i = 0; i < commands.size(); i++) {
      Command command = (Command) commands.get(i);
      sb.append("  " + command + "\n");
    }
    return sb.toString();
  }

  /**
   * Factory method that creates a new <code>Test</code> from a
   * portion of an XML DOM tree.
   */
  static Test fromXML(Element root) {
    if (!root.getTagName().equals("test")) {
      String s = "Expected <test>, got <" + root.getTagName() + ">";
      throw new IllegalArgumentException(s);
    }

    String name = null;
    String description = null;
    List commands = new ArrayList();
    
    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      if (!(node instanceof Element)) {
        continue;
      }

      Element child = (Element) node;
      if (child.getTagName().equals("name")) {
        name = Command.extractTextFrom(child);

      } else if (child.getTagName().equals("description")) {
        description = Command.extractTextFrom(child);

      } else if (child.getTagName().equals("run")) {
        commands.add(RunCommand.fromXML(child));
                    
      } else if (child.getTagName().equals("copy")) {
        commands.add(CopyCommand.fromXML(child));
                    
      } else if (child.getTagName().equals("cat")) {
        commands.add(CatCommand.fromXML(child));

      } else {
        String s = "Encountered unknown tag: <" + child.getTagName() + 
          "> while parsing a <test>";
        throw new IllegalArgumentException(s);
      }
    }

    Test test = new Test(name, description);
    for (int i = 0; i < commands.size(); i++) {
      test.addCommand((Command) commands.get(i));
    }

    return test;
  }

  /**
   * Prints usage information about this program
   */
  private static void usage() {
    PrintStream err = System.err;
    err.println("usage: java Test className testName args*");
    System.exit(1);
  }

  /**
   * Test program that loads a class from the system class path and
   * executes its main method
   */
  public static void main(String[] args) {
    if (args.length < 2) {
      usage();
    }

    String className = null;
    String testName = null;
    List params = new ArrayList();

    for (int i = 0; i < args.length; i++) {
      if (className == null) {
        className = args[i];

      } else if (testName == null) {
        testName = args[i];

      } else {
        params.add(args[i]);
      }
    }

    // Remember that main takes one arg, an array of Strings
    Object[] argsArray = new Object[1];
    argsArray[0] = (String[]) params.toArray(new String[0]);

    Test test = new Test(testName, "Some description");
    Command command = new RunCommand(className, "main", argsArray,
                                     false, false);
    test.addCommand(command);
    test.setClassLoader(ClassLoader.getSystemClassLoader());

    Report report = new Report(new PrintWriter(System.out, true));
    test.execute(report, new Properties());
  }

}
