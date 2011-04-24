package edu.pdx.cs410J.security;

import java.io.*;

/**
 * This program writes a file in the user's home directory.  It is
 * used how we can grant code certain privileges based on when it came
 * from. 
 */
public class WriteToHomeDir {

  public static void main(String[] args) {
    String homeDir = System.getProperty("user.home");
    String fileName = "Hello";
    File file = new File(homeDir, fileName);
    try {
      PrintWriter pw = new PrintWriter(new FileWriter(file), true);
      pw.println("Hello There!");
      pw.close();

    } catch (IOException ex) {
      ex.printStackTrace(System.err);
      System.exit(1);
    }
  }
}
