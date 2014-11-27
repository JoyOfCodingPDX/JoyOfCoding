package edu.pdx.cs410J.grader;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GraderTools {

  public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    String tool = null;
    List<String> toolArgs = new ArrayList<>();
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

    configureLogger();

    invokeMainMethod(getToolClass(tool), toolArgs.toArray(new String[toolArgs.size()]));
  }

  private static void configureLogger() {
    // Oddly enough, attempts to access the Logger end up causes a ClassNotFoundException
    System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s %5$s%6$s%n");
  }

  private static Class getToolClass(String tool) {
    switch (tool) {
      case "gradebook":
        return GradeBookGUI.class;

      case "fetch":
        return FetchAndProcessGraderEmail.class;

      case "importFromD2L" :
        return GradesFromD2LImporter.class;

      case "importFromProjectReports" :
        return ProjectGradesImporter.class;

      case "mailFileToStudent" :
        return StudentFileMailer.class;

      default:
        usage("Unknown tool: " + tool);
        return null;
    }
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
    err.println("  tool                        The tool to execute");
    err.println("    gradebook                 The Grade Book GUI");
    err.println("    fetch                     Fetch student surveys or projects from the Grader's");
    err.println("                              emails account");
    err.println("    importFromD2L             Import grades from a D2L CSV");
    err.println("    importFromProjectReports  Import grades from graded project reports");
    err.println("    mailFileToStudent         Email text files to students");
    err.println("  toolArg                     A command line argument to send to the tool");
    err.println();

    System.exit(1);
  }
}
