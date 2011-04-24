package edu.pdx.cs410J.core;

import java.util.*;

/**
 * This classes parses strings from the command line using a
 * <code>StringTokenizer</code>.
 */
public class ParseString {

  /**
   * The second <code>String</code> from the command line contains the
   * parsing delimiters.
   */
  public static void main(String[] args) {
    String string = args[0];
    String delimit = args[1];
    StringTokenizer st;
    st = new StringTokenizer(string, delimit);

    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      System.out.println("Token: " + token);
    }
  }
}
