package edu.pdx.cs410J.core;

import java.text.*;
import java.util.*;

/**
 * This program demonstrates how to use the {@link
 * java.text.SimpleDateFormat} class to format and parse dates.
 */
public class SimpleDate {

  public static void main(String[] args) {
    DateFormat df = new SimpleDateFormat(args[0]);
    if (args.length > 1) {
      try {
        df.setLenient(true);
        System.out.println(df.parse(args[1]));

      } catch (ParseException ex) {
        System.err.println("Malformed date: " + args[1]);
      }
      
    } else {
      Date now = new Date();
      System.out.println(df.format(now));
    }
  }

}
