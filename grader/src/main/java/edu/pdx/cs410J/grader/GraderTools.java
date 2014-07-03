package edu.pdx.cs410J.grader;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GraderTools {

  public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    String tool = null;
    List<String> toolArgs = new ArrayList<String>();
    for (String arg : args) {
      if (tool == null) {
        tool = arg;

      } else {
        toolArgs.add(arg);
      }
    }

    if (tool == null) {
      usage("Missing tool");
    }

    Class toolClass;
    if (tool.equals("gradebook")) {
      toolClass = GradeBookGUI.class;

    } else if (tool.equals("fetch")) {
      toolClass = FetchAndProcessGraderEmail.class;

    } else {
      usage("Unknown tool: " + tool);
      return;
    }

    invokeMainMethod(toolClass, toolArgs.toArray(new String[toolArgs.size()]));
  }

  @SuppressWarnings("unchecked")
  private static void invokeMainMethod(Class aClass, String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method main = aClass.getMethod("main", args.getClass());
    main.invoke(aClass, new Object[] { args });

  }

  private static void usage(String message) {
    PrintStream err = System.err;
    err.println("** " + message);
    err.println();
    err.println("Executes one of the Grader tools");
    err.println();
    err.println("usage: GraderTools tool toolArg*");
    err.println("  tool           The tool to execute");
    err.println("    gradebook    The Grade Book GUI");
    err.println("    fetch        Fetch student surveys or project from the Grader's emails account");
    err.println("  toolArg        A command line argument to send to the tool");
    err.println();

    System.exit(1);
  }
}
