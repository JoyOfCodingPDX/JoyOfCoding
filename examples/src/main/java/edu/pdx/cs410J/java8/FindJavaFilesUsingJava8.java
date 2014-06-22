package edu.pdx.cs410J.java8;

import java.io.File;
import java.util.Arrays;

/**
 * This program demonstrates how Lambdas in Java 8 greatly simplify using
 * "functional interfaces" like <code>FileFilter</code> and
 * <code>FileNameFilter</code>.
 *
 * @since Summer 2014
 */
public class FindJavaFilesUsingJava8 {

  /**
   * Prints out the names all of the Java source files in a directory
   * and then recurses over subdirectories.
   */
  private static void findJavaFiles(File directory) {
    File[] javaFiles = directory.listFiles((dir, name) -> name.endsWith(".java"));
    Arrays.asList(javaFiles).forEach(System.out::println);

    File[] subdirectories = directory.listFiles(File::isDirectory);
    Arrays.asList(subdirectories).forEach(FindJavaFilesUsingJava8::findJavaFiles);
  }

  /**
   * The one command line parameter is the directory in which to start
   * the search.
   */
  public static void main(String[] args) {
    File file = new File(args[0]);
    if (file.isDirectory()) {
      // Look for Java files
      findJavaFiles(file);

    } else {
      System.err.println(file + " is not a directory");
    }
  }

}
