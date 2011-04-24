package edu.pdx.cs410J.reflect;

import java.io.*;
import java.lang.reflect.*;

/**
 * This program uses Java reflection to describe a class, its methods,
 * and fields.
 */
public class DescribeClass {
  private static PrintStream out = System.out;
  private static PrintStream err = System.err;

  /**
   * Prints a description of a field
   */
  private static void describe(Field f) {
    out.print("  ");
    out.print(Modifier.toString(f.getModifiers()));
    out.print(" ");
    out.print(f.getType().getName());
    out.print(" ");
    out.println(f.getName());
  }

  /**
   * Prints a description of a method
   */
  private static void describe(Method m) {
    out.print("  ");
    out.print(Modifier.toString(m.getModifiers()));
    out.print(" ");
    out.print(m.getReturnType().getName());
    out.print(" ");
    out.print(m.getName());
    out.print("(");

    Class[] params = m.getParameterTypes();
    for (int i = 0; i < params.length; i++) {
      out.print(params[i].getName());
      if (i < params.length - 1)
        out.print(", ");
    }
    out.println(")");

    Class[] exs = m.getExceptionTypes();
    if (exs.length > 0) {
      out.print("    throws ");
      for (int i = 0; i < exs.length; i++) {
        out.print(exs[i].getName());
        if (i < exs.length - 1)
          out.print(", ");
      }
      out.println("");
    }
  }

  /**
   * Prints a description of a class
   */
  private static void describe(Class c) {
    out.print(Modifier.toString(c.getModifiers()));
    out.print(" ");
    out.print((c.isInterface() ? "interface " : "class "));
    out.println(c.getName());

    Class superclass = c.getSuperclass();
    if (superclass != null) {
      out.print("  extends ");
      out.println(superclass.getName());
    }

    Class[] ifaces = c.getInterfaces();
    if (ifaces.length > 0) {
      out.print("  implements ");
      for (int i = 0; i < ifaces.length; i++) {
        out.print(ifaces[i].getName());
        if (i < ifaces.length - 1)
          out.print(", ");
      }
      out.println("");
    }

    out.println("{");

    Field[] fields = c.getDeclaredFields();
    for (int i = 0; i < fields.length; i++) {
      describe(fields[i]);
    }

    out.println("");

    Method[] methods = c.getDeclaredMethods();
    for (int i = 0; i < methods.length; i++) {
      describe(methods[i]);
    }

    out.println("}");

  }

  /**
   * Main program that reads the name of a class from the command
   * line, loads it, and prints out a description of it.
   */
  public static void main(String[] args) {
    String className = args[0];
    try {
      Class c = Class.forName(className);
      describe(c);

    } catch (ClassNotFoundException ex) {
      err.println("Could not load " + className);
    }
  }

}
