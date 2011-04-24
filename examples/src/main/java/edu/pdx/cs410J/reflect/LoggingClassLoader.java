package edu.pdx.cs410J.reflect;

import java.io.File;
import java.net.*;

/**
 * This <code>ClassLoader</code> prints out the name of every class
 * that it (or its parent) loads.
 */
public class LoggingClassLoader extends URLClassLoader {

  /**
   * Creates a <code>LoggingClassLoader</code> that loads classes from
   * a given array of URLs.  However, before this class loader
   * attempts to load the class, it will delegate to its parent class
   * loader.
   */
  public LoggingClassLoader(URL[] urls, ClassLoader parent) {
    super(urls, parent);
  }

  /**
   * Invoked as this class loader is loading a class
   */
  public Class loadClass(String className) 
    throws ClassNotFoundException {

    System.out.println("Loading " + className);
    return super.loadClass(className);
  }
  
  /**
   * Main program that uses a <code>LoggingClassLoader</code> to load
   * the class with the given name from a given location.
   */
  public static void main(String[] args) {
    File file = new File(args[0]);
    String className = args[1];

    try {
      URL[] urls = new URL[] { file.toURL() };
      ClassLoader parent = ClassLoader.getSystemClassLoader();
      ClassLoader cl = new LoggingClassLoader(urls, parent);
      cl.loadClass(className);

    } catch (MalformedURLException ex) {
      String s = "Bad URL: " + ex;
      System.err.println(s);

    } catch (ClassNotFoundException ex) {
      String s = "Could not find class " + className;
      System.err.println(s);
    }
  }

}
