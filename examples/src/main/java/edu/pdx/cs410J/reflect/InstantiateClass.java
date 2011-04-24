package edu.pdx.cs410J.reflect;

/**
 * This program reads the name of a class with a zero-argument
 * constructor and uses Java reflection to load and instantiate the
 * class.
 */
public class InstantiateClass {

  public static void main(String[] args) {
    ClassLoader cl = ClassLoader.getSystemClassLoader();
    try {
      Class c = cl.loadClass(args[0]); 
      Object o = c.newInstance();
      System.out.println(o);

    } catch (InstantiationException ex) {
      String s = "Could not instantiate " + args[0];
      System.err.println(s);

    } catch (IllegalAccessException ex) {
      String s = "No public constructor for " + args[0];
      System.err.println(s);

    } catch (ClassNotFoundException ex) {
      String s = "Could not find " + args[0];
      System.err.println(s);
    }
  }

}
