package edu.pdx.cs.joy.airline;

import com.google.common.annotations.VisibleForTesting;

/**
 * The main class for the Airline Project
 */
public class Project1 {

  @VisibleForTesting
  static boolean isValidDateAndTime(String dateAndTime) {
    return true;
  }

  public static void main(String[] args) {
    Flight flight = new Flight();  // Refer to one of Dave's classes so that we can be sure it is on the classpath
    System.err.println("Missing airline information");
    for (String arg : args) {
      System.out.println(arg);
    }
  }

}