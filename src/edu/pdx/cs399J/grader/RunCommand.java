package edu.pdx.cs410J.grader;

import java.lang.reflect.*;
import java.util.*;
import org.w3c.dom.*;

/**
 * A <code>RunCommand</code> is a command that runs a static method of
 * a Java class and displays its output on a <code>Report</code>.
 * Special care must be taken to ensure that classes are initialized
 * at the appropriate times and that untrusted code runs with the
 * correct permissions.
 *
 * @author David Whitlock
 */
public class RunCommand extends Command {

  /** The name of class whose static method is being executed */
  private String className;

  /** The static method being executed */
  private String methodName;

  /** The arguments to the static method */
  private Object[] args;

  /** Should this method run in its own thread? */
  private boolean inOwnThread;

  /** Do we print the method's result to the report? */
  private boolean printResult;

  /**
   * Creates a new <code>RunCommand</code> that executes a given
   * static method in a given class with a given set of arguments.
   * Optionally, the method may be run in its own thread and the
   * method's result may be printed to the report.
   */
  public RunCommand(String className, String methodName, 
                    Object[] args, boolean inOwnThread, 
                    boolean printResult) {
    this.className = className;
    this.methodName = methodName;
    this.args = args;
    this.inOwnThread = inOwnThread;
    this.printResult = printResult;
  }

  /**
   * Runs the given static method
   */
  void execute(final Report report, final Properties props) {
    final String className = expandMacros(this.className, props);
    final String methodName = expandMacros(this.methodName, props);
    final Object[] args = expandMacros(this.args, props);

    Runnable r = new 
      Runnable() {
        public void run() {
          ClassLoader loader = RunCommand.this.getClassLoader();

          // First load the class and the desired method
          Class c = null;
          try {
            c = Class.forName(className, true, loader);

          } catch (ClassNotFoundException ex) {
            report.logException("While executing " + className + "." +
                                methodName + "()", ex);
            return;
          }

          Method m = null;
          try {
            Class[] paramTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
              paramTypes[i] = args[i].getClass();
            }

            m = c.getMethod(methodName, paramTypes);

          } catch (NoSuchMethodException ex) {
            String s = "Could not find method " + methodName + 
              " in " + className;
            report.logException(s, ex);
            return;
          }

          // Print out some information
          report.printBanner("Executing " + className + "." +
                             methodName + "()", '-');

          // Print out the arguments if we're running a main
          if (methodName.equals("main")) {
            String[] args;

            if (RunCommand.this.args.length > 0) {
              args = (String[]) RunCommand.this.args[0];
            } else {
              args = new String[0];
            }

            // Print out args
            StringBuffer sb = new StringBuffer();
            for (int j = 0; j < args.length; j++) {
              boolean needQuotes = false;

              if (args[j].indexOf(" ") != -1) {
                needQuotes = true;
              }

              if (needQuotes) {
                sb.append("\"");
              }

              sb.append(args[j]);
              
              if (needQuotes) {
                sb.append("\"");
              }
              
              sb.append(" ");
            }
 
            report.println("Command line: " + sb.toString().trim());
            report.println("");
            report.println("Program output:");
          }

          // Run the method
          Object result = null;
          try {
            try {
              result = m.invoke(null, args);

            } catch (InvocationTargetException ex) {
              throw ex.getTargetException();
            }

          } catch (TesterExitException ex) {
            // That's okay, we're just exiting from one invocation of the
            // main method

          } catch (IllegalAccessException ex) {
            String s = "IllegalAccessException while invoking " +
              className + "." + methodName + "()";
            report.logException(s, ex);
            result = null;

          } catch (Exception ex) {
            // Log everything else
            String s = "** Exception while invoking " + className +
              "." + methodName + "()";
            report.logException(s, ex);
            result = null;

          } catch (Throwable ex) {
            // EEEP!!!
            System.err.println("*** SEVERE ERROR!!!");
            ex.printStackTrace(System.err);
            System.exit(1);
          }

          System.out.flush();
          System.err.flush();
          
          report.println("");
          report.flush();

          if (printResult) {
            report.println(result);
          }
        }
      };

    if (this.inOwnThread) {
      Thread t = new Thread(r);
      t.start();

    } else {
      r.run();
    }
  }

  /**
   * Returns a brief textual representation of this
   * <code>RunCommand</code> 
   */
  public String toString() {
    return "run " + className + "." + methodName + "( with " +
           args.length + " args" + 
           (this.inOwnThread ? " inOwnThread" : "") +
           (this.printResult ? " printResult" : "");
  }

  /**
   * Parses a long string and from it make an array of strings.  Text
   * between double quotes counts as one string.
   */
  private static String[] extractStringsFrom(String string) {
    int startIndex = 0;
    boolean inQuoted = false;
    ArrayList strings = new ArrayList();

    for (int i = startIndex; 
        i < string.length() && startIndex < string.length(); i++) {
      char c = string.charAt(i);
      if (c == '\"') {
        // end the current word, don't put quotes in the word
        String s = string.substring(startIndex, i).trim();
        if (!s.equals("")) {
          strings.add(s);
        }
        startIndex = i + 1;
        inQuoted = !inQuoted;

      } else if (Character.isWhitespace(c) && !inQuoted) {
        // end the current word
        String s = string.substring(startIndex, i).trim();
        if (!s.equals("")) {
          strings.add(s);
        }
        startIndex = i + 1;
      }
    }

    if (inQuoted) {
      String s = "Unmatched double quotes in: " + string;
      throw new IllegalArgumentException(s);

    } else {
      String s = string.substring(startIndex).trim();
      if (!s.equals("")) {
        strings.add(s);
      }
    }

    return (String[]) strings.toArray(new String[0]);
  }

  /**
   * Converts a <code>String</code> to some type.  The type must have
   * a one-arg constructor that has a <code>String</code> argument.
   */
  private static Object convertStringToType(String string, 
                                            String typeName) {
    try {
      Class[] types = { String.class };
      Constructor init = Class.forName(typeName).getConstructor(types);
      Object[] args = { string };
      return init.newInstance(args);

    } catch (NoSuchMethodException ex) {
      String s = "Class " + typeName + 
        " does not have a constructor with a single String arg";
      throw new IllegalArgumentException(s);

    } catch (ClassNotFoundException ex) {
      String s = "Could not find class: " + typeName;
      throw new IllegalArgumentException(s);

    } catch (Exception ex) {
      String s = "While instantiating a " + typeName + " with " +
        string + ": " + ex;
      throw new IllegalArgumentException(s);
    }
    
  }

  /**
   * Extracts an array of method arguments from a portion of an XML
   * DOM tree.
   */
  private static Object[] extractArgsFrom(Element root) {
    if (!root.getTagName().equals("args")) {
      String s = "Expected <run>, got <" + root.getTagName() + ">";
      throw new IllegalArgumentException(s);
    }

    List args = new ArrayList();

    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      if (!(node instanceof Element)) {
        continue;
      }

      Element child = (Element) node;
      if (child.getTagName().equals("obj")) {
        String type = child.getAttribute("type");
        String text = Command.extractTextFrom(child);
        args.add(convertStringToType(text, type));        

      } else if (child.getTagName().equals("array")) {
        String type = child.getAttribute("type");

        String text = Command.extractTextFrom(child);
        String[] strings = extractStringsFrom(text);
        Object[] array = new Object[strings.length];
        for (int j = 0; j < strings.length; j++) {
          array[j] = convertStringToType(strings[j], type);
        }
        args.add(array);
      }
    }

    return args.toArray();
  }

  /**
   * Factory method that creates a new <code>RunCommand</code> from a
   * portion of an XML DOM tree.
   */
  static RunCommand fromXML(Element root) {
    if (!root.getTagName().equals("run")) {
      String s = "Expected <run>, got <" + root.getTagName() + ">";
      throw new IllegalArgumentException(s);
    }

    String className = null;
    String methodName = null;
    Object[] args = null;
    boolean inOwnThread = false;
    boolean printResult = false;

    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      if (!(node instanceof Element)) {
        continue;
      }

      Element child = (Element) node;
      if (child.getTagName().equals("class")) {
        className = Command.extractTextFrom(child);

      } else if (child.getTagName().equals("method")) {
        methodName = Command.extractTextFrom(child);

      } else if (child.getTagName().equals("args")) {
        args = extractArgsFrom(child);

      } else {
        String s = "Encountered unknown tag: <" + child.getTagName() + 
          "> while parsing a <run>";
        throw new IllegalArgumentException(s);
      }
    }

    return(new RunCommand(className, methodName, args, inOwnThread,
                          printResult));
  }

  public static void main(String[] args) {
    String s = "This is a \"test of \" how we \"manipulate\" stuff";
    String[] strings = extractStringsFrom(s);
    for (int i = 0; i < strings.length; i++) {
      System.out.println(strings[i]);
    }

    System.out.println(convertStringToType("2", "java.lang.Double"));
  }

}

