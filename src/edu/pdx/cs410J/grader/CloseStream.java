package edu.pdx.cs410J.grader;

import java.io.*;

/**
 * Can we still write to a stream after we close it?
 */
public class CloseStream {

  public static void main(String[] args) {
    FileOutputStream fos = null;

    try {
      fos = new FileOutputStream("TEST");

    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
      System.exit(1);
    }

    PrintWriter pw1 = new PrintWriter(fos, true);

    pw1.println("First line");

    PrintWriter pw2 = new PrintWriter(fos, true);

    try {
      fos.close();

    } catch (IOException ex) {
      ex.printStackTrace();
      System.exit(1);
    }

    pw2.println("Second line");

    System.out.println("error = " + pw2.checkError());
  }

}
