package edu.pdx.cs399J.grader;

import java.util.*;
import org.w3c.dom.*;

/**
 * A <code>Command</code> is something that is executed as part of
 * grading an assignment in CS399J.
 */
public abstract class Command {

  /** ClassLoader used to load classes while this command executes */
  private ClassLoader loader;

  /**
   * Sets the <code>ClassLoader</code> used by this command when it
   * executes
   */
  void setClassLoader(ClassLoader loader) {
    this.loader = loader;
  }

  /**
   * Returns the <code>ClassLoader</code> used by this command when it
   * executes.
   */
  ClassLoader getClassLoader() {
    return this.loader;
  }

  /**
   * Executes this command, prints its results to a report, and
   * performs string subsitution based on a set of properties.
   */
  abstract void execute(Report report, Properties macros);

  /**
   * Expands any macros such as {STUDENT} in a String
   */
  String expandMacros(String s, Properties props) {
    Iterator keys = props.keySet().iterator();
    while (keys.hasNext()) {
      String key = (String) keys.next();
      String value = (String) props.get(key);
      int index = s.indexOf(key);
      for (int j = 0; j < 20 && index != -1; j++) {
        // Replace key with value
        StringBuffer sb = new StringBuffer(s);
        sb.replace(index, index + key.length(), value);
        s = sb.toString();
        
        index = s.indexOf(key);
      }
    } 

    return s;
  }

  /**
   * Go through an array of <code>Object</code>s and expand macros
   * such as "{STUDENT}" in any <code>String</code> arguments.
   *
   * @return A clone of the <code>args</code> array with macros
   *         expanded
   */
  Object[] expandMacros(Object[] args, Properties props) {
    Object[] newArgs = new Object[args.length];

    for (int i = 0; i < args.length; i++) {
      if (args[i] instanceof String) {
        newArgs[i] = expandMacros((String) args[i], props);
      } else {
        newArgs[i] = args[i];
      }
    }

    return newArgs;
  }

  /**
   * Extracts the text from an <code>Element</code>.
   */
  static String extractTextFrom(Element element) {
    Text text = (Text) element.getFirstChild();
    return (text == null ? "" : text.getData());
  }

}
