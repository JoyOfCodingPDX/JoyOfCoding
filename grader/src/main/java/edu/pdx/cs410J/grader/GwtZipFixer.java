package edu.pdx.cs410J.grader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class GwtZipFixer {

  private static final Logger logger = LoggerFactory.getLogger("edu.pdx.cs410J.grader");

  public static void main(String[] args) {
    String outputDirectoryName = null;
    List<String> zipFileNames = new ArrayList<>();

    for (String arg : args) {
      if (outputDirectoryName == null) {
        outputDirectoryName = arg;

      } else {
        zipFileNames.add(arg);
      }
    }

    if (outputDirectoryName == null) {
      usage("Missing output directory");
    }

    if (zipFileNames.isEmpty()) {
      usage("Missing zip file");
    }
  }

  private static void usage(String message) {
    PrintStream err = System.err;

    err.println("+++ " + message);
    err.println();
    err.println("usage: java GwtZipFixer outputDirectory zipFile+");
    err.println("    outputDirectory     Name of direct into which fixed zip files should be written");
    err.println("    zipFile             Name of zip file to be fixed");
    err.println();
    err.println("Fixes the contents of a zip file submitted for the GWT ");
    err.println("project so that it will work with the grading scripts");
    err.println();

    System.exit(1);
  }
}
