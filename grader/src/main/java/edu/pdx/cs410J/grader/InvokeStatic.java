package edu.pdx.cs410J.grader;

import java.lang.reflect.*;
import java.io.*;
import java.util.*;

/**
 * This program uses Java reflection to invoke a static method of a
 * given class.
 */
public class InvokeStatic {
  private static PrintStream out = System.out;
  private static PrintStream err = System.err;
  private static final boolean VERBOSE = 
    Boolean.getBoolean("InvokeStatic.VERBOSE");
  
  /**
   * Turns a string into the an instance of the given type.  Array
   * elements are separated by a space.
   */
  private static Object parseParam(String param, Class type) 
    throws Throwable {

    if (type.isArray()) {
      Class elementType = type.getComponentType();
      StringTokenizer st = new StringTokenizer(param, " ");
      List<Object> elements = new ArrayList<Object>();
      while (st.hasMoreTokens()) {
        elements.add(parseParam(st.nextToken(), elementType));
      }

      Object array = Array.newInstance(elementType, elements.size());
      for (int i = 0; i < elements.size(); i++) {
        Array.set(array, i, elements.get(i));
      }
      return array;

    } else {
      // The type must have a constructor that takes a string argument
      if (type.equals(Character.TYPE)) {
	type = Character.class;

      } else if (type.equals(Boolean.TYPE)) {
	type = Boolean.class;

      } else if (type.equals(Byte.TYPE)) {
	type = Byte.class;

      } else if (type.equals(Short.TYPE)) {
	type = Short.class;

      } else if (type.equals(Integer.TYPE)) {
	type = Integer.class;

      } else if (type.equals(Long.TYPE)) {
	type = Long.class;

      } else if (type.equals(Float.TYPE)) {
	type = Float.class;

      } else if (type.equals(Double.TYPE)) {
	type = Double.class;
      }

      Constructor init = 
        type.getConstructor(new Class[] { String.class });
      return init.newInstance(new Object[] { param });
    }
  }

  /**
   * This method converts a string into a Class type.  Array types are
   * denoted with a preceeding "[".
   */
  private static Class parseType(String name)
    throws ClassNotFoundException {
    
    int lastBracket = name.lastIndexOf('[');
    if (lastBracket == -1) {
      // Not an array type

      if (name.equals("char")) {
	return Character.TYPE;

      } else if (name.equals("boolean")) {
	return Boolean.TYPE;

      } else if (name.equals("byte")) {
	return Byte.TYPE;

      } else if (name.equals("short")) {
	return Short.TYPE;

      } else if (name.equals("int")) {
	return Integer.TYPE;

      } else if (name.equals("long")) {
	return Long.TYPE;

      } else if (name.equals("float")) {
	return Float.TYPE;

      } else if (name.equals("double")) {
	return Double.TYPE;

      } else {
	return Class.forName(name);
      }

    } else {
      // Array type
      String className = name.substring(lastBracket + 1);
      Class elementType = parseType(className);
      int[] dimensions = new int[lastBracket + 1];
      for (int i = 0; i < dimensions.length; i++) {
        dimensions[i] = 0;
      }
      return Array.newInstance(elementType, dimensions).getClass();
    }
  }

  /**
   * Returns a default value for a given type.  If the type is an
   * array type, then it returns an empty array of that type.
   * Otherwise, it returns <code>null</code>.
   */
  private static Object getDefaultValueFor(Class type) {
    if (type.isArray()) {
      return Array.newInstance(type.getComponentType(), 0);

    } else {
      return null;
    }
  }

  /**
   * Prints usage information for this program
   */
  private static void usage(String s) {
    err.println("\n** " + s + "\n");
    err.println("usage java InvokeStatic className methodName " +
                "[paramTypes] [(param)*]");
    err.println("  className   The name of the class containing the "
                + "static method");
    err.println("  methodName  The name of the static method");
    err.println("  paramTypes  The types of the method's arguments " +
                "(defaults to an array of");
    err.println("  param       An argument to the method (defaults "
                + "to an empty array of");
    err.println("              Strings");
    err.println("              Strings");
    err.println("");
    System.exit(1);
  }

  public static void main(String[] args) throws Throwable {
    String className = null;
    String methodName = null;
    String paramTypes = null;
    List<String> params = new ArrayList<String>();

    // Parse the command line
    for (int i = 0; i < args.length; i++) {
      if (className == null) {
        className = args[i];

      } else if (methodName == null) {
        methodName = args[i];

      } else if (paramTypes == null) {
        paramTypes = args[i];

      } else {
        // It's an arguement
        params.add(args[i]);
      }
    }

    if (className == null) {
      usage("Missing class name");
    }

    if (methodName == null) {
      usage("Missing method name");
    }

    if (paramTypes == null) {
      paramTypes = ""; // "[java.lang.String";
    }

//      if (params.isEmpty()) {
//        params.add("");
//      }

    // Translate the methodArgsTypes into types
    List<Object> argsTypes = new ArrayList<Object>();
    StringTokenizer st = new StringTokenizer(paramTypes, ";");
    while (st.hasMoreTokens()) {
      argsTypes.add(parseType(st.nextToken()));
    }

    Class[] types = (Class[]) argsTypes.toArray(new Class[0]);
    List<Object> actuals = new ArrayList<Object>();

    // Translate the parameters into objects
    for (int i = 0; i < types.length; i++) {
      Class type = types[i];
      if (params.isEmpty()) {
	actuals.add(getDefaultValueFor(type));

      } else if (type.isArray()) {
	// The params are elements of the array
	Class elementType = type.getComponentType();
	Object array = Array.newInstance(elementType, params.size());

	for (int j = 0; j < params.size(); j++) {
	  String param = (String) params.get(j);
	  Array.set(array, j, parseParam(param, elementType));
	}

	actuals.add(array);

      } else {
	String param = (String) params.get(i);
	actuals.add(parseParam(param, type));
      }
    }

    // Make sure that the number of parameter types equals the number
    // of parameters
    if (!actuals.isEmpty() && types.length != actuals.size()) {
      String s = "Param mismatch: " + types.length + 
	" param types and " + actuals.size() + " params";
      throw new IllegalArgumentException(s);
    }

    if (VERBOSE) {
      StringBuffer sb = new StringBuffer();
      sb.append("Invoking ");
      sb.append(className);
      sb.append(".");
      sb.append(methodName);
      sb.append("(");
      for (int i = 0; i < types.length; i++) {
        sb.append(types[i].getName());
        if (i != types.length - 1) {
          sb.append(", ");
        }
      }
      sb.append(")");
      out.println(sb.toString());
      out.println("params are " + actuals);
    }

    Class c = Class.forName(className);
    Method m = c.getDeclaredMethod(methodName, types);
    m.setAccessible(true);

    try {
      Object value = m.invoke(null, actuals.toArray());

      if (!m.getReturnType().equals(Void.TYPE)) {
	System.out.println("\nMethod returned: " + value);
      }

    } catch (InvocationTargetException ex) {
      throw ex.getTargetException();
    }
  }
}
