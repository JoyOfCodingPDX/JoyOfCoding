package edu.pdx.cs410J.core;

import java.io.*;

/**
 * This program reads text from the console until the user enters
 * <code>-1</code> at which point it prints what the user has entered.
 * It demonstrates the <code>BufferedReader</code>,
 * <code>InputStreamReader</code>, and <code>StringWriter</code>
 * classes.
 */
public class ReadFromConsole {

  public static void main(String[] args) {
    InputStreamReader isr = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(isr);
    StringWriter text = new StringWriter();

    while (true) {
      try {
	// Read a line from the console
	String line = br.readLine();

	if(line.equals("-1")) {
	  // All done with input
	  break;

	} else {
	  text.write(line + " ");
	}

      } catch (IOException ex) {
	System.err.println("** " + ex);
	System.exit(1);
      }
    }

    // Print out what was entered
    System.out.println(text);
  }

}
