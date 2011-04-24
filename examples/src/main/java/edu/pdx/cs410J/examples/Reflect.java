package edu.pdx.cs410J.examples;

import java.io.*;
import java.lang.reflect.*;

/**
 * This class demonstrates Java's reflection mechanism by loading a
 * class and then printing out information about its fields and
 * methods.
 *
 * @author David Whitlock
 */
public class Reflect {

  private static PrintWriter out = new PrintWriter(System.out, true);
  private static PrintWriter err = new PrintWriter(System.err, true);

  /**
   * Displays usage information about this program.
   */
  private static void usage() {
    err.println("usage: java Reflect className");
    System.exit(1);
  }

  /**
   * Returns a <code>String</code> representing a given set of access
   * modifiers.
   */
  private static String modifiersString(int modifiers) {
    StringBuffer sb = new StringBuffer();

    if (Modifier.isPublic(modifiers)) {
      sb.append("public ");
    } 

    if (Modifier.isProtected(modifiers)) {
      sb.append("protected ");
    }

    if (Modifier.isPrivate(modifiers)) {
      sb.append("private ");
    }

    if (Modifier.isStatic(modifiers)) {
      sb.append("static ");
    }

    if (Modifier.isFinal(modifiers)) {
      sb.append("final ");
    }

    if (Modifier.isNative(modifiers)) {
      sb.append("native ");
    }

    if (Modifier.isAbstract(modifiers)) {
      sb.append("abstract ");
    }

    if (Modifier.isSynchronized(modifiers)) {
      sb.append("synchronized ");
    }

    if (Modifier.isVolatile(modifiers)) {
      sb.append("volatile ");
    }

    return sb.toString();
  }

  /**
   * Returns a string representation of a class name.  Takes array
   * classes into account.
   */
  private static String getTypeName(Class type) {
    String typeName;
    if (type.isArray()) {
      typeName = type.getComponentType().getName() + "[]";

    } else {
      typeName = type.getName();
    }

    return typeName;
  }

  /**
   * Reads a class name from the command line, loads it and prints out
   * information about it.
   */
  public static void main(String[] args) {
    if (args.length < 1) {
      err.println("** Missing class name");
      usage();
    }

    Class c = null;

    try {
      c = Class.forName(args[0]);

    } catch (ClassNotFoundException ex) {
      err.println("** Could not find class " + args[0]);
    }

    // Print name of class and its superclass
    out.print(modifiersString(c.getModifiers()));
    out.println("class " + getTypeName(c));
    if (c.getSuperclass() != null) {
      out.print("    extends " + getTypeName(c.getSuperclass()));
    }
    out.println("");

    // Print out any interfaces class implements
    Class[] interfaces = c.getInterfaces();
    if (interfaces.length > 0) {
      out.print("    implements ");
      for (int i = 0; i < interfaces.length; i++) {
	out.print(interfaces[i].getName());

	if(i < interfaces.length - 1) {
	  out.print(", ");
	}
      }
      out.println("");
    }

    out.println("{");

    // Print descriptions of fields (not including inherited fields)
    Field[] fields = c.getDeclaredFields();
    for (int i = 0; i < fields.length; i++) {
      Field field = fields[i];

      out.print("  " + modifiersString(field.getModifiers()));

      String typeName = getTypeName(field.getType());
      
      out.println(typeName + " " + field.getName() + ";");
    }
    
    out.println("");

    // Print description of constructors
    Constructor[] constructors = c.getDeclaredConstructors();
    for (int i = 0; i < constructors.length; i++) {
      Constructor con = constructors[i];

      out.print("  " + modifiersString(con.getModifiers()));
      out.print(con.getName() + "(");

      Class[] params = con.getParameterTypes();
      for (int j = 0; j < params.length; j++) {
	Class param = params[j];
	out.print(getTypeName(param));

	if(j < params.length - 1) {
	  out.print(", ");
	}
      }

      out.print(")");

      Class[] exceptions = con.getExceptionTypes();
      if (exceptions.length > 0) {
	out.print("\n    throws ");
	for(int j = 0; j < exceptions.length; j++) {
	  Class ex = exceptions[j];
	  out.print(getTypeName(ex));
	  if (j < exceptions.length - 1) {
	    out.print(", ");
	  }
	}
      }
      out.println(";");
    }

    out.println("");

    // Print description of methods
    Method[] methods = c.getDeclaredMethods();
    for (int i = 0; i < methods.length; i++) {
      Method method = methods[i];

      out.print("  " + modifiersString(method.getModifiers()));
      out.print(getTypeName(method.getReturnType()) + " ");
      out.print(method.getName() + "(");

      Class[] params = method.getParameterTypes();
      for (int j = 0; j < params.length; j++) {
        Class param = params[j];
        out.print(getTypeName(param));

        if (j < params.length - 1) {
          out.print(", ");
        }
      }

      out.print(")");

      Class[] exceptions = method.getExceptionTypes();
      if (exceptions.length > 0) {
        out.print("\n    throws ");
        for (int j = 0; j < exceptions.length; j++) {
          Class ex = exceptions[j];
          out.print(getTypeName(ex));
          if (j < exceptions.length - 1) {
            out.print(", ");
          }
        }
      }
      out.println(";");
    }

    // All done
    out.println("}");
  }

}
