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
    Date now = new Date();
    System.out.println(df.format(now));
  }

}
