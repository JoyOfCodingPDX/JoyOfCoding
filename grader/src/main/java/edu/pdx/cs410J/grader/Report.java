package edu.pdx.cs410J.grader;

import java.io.*;

/**
 * This class represents a report that is generated as a result of
 * running a student's program.
 *
 * @author David Whitlock
 */
public class Report {

  /** The original <code>System.err</code> */
  PrintStream consoleErr = System.err;

  /** The original <code>System.out</code> */
  PrintStream consoleOut = System.out;

  /** The number of character columns in the log file. */
  private static final int WIDTH = 72;

  /** Write output of tests and other stuff to here. */
  private PrintWriter log;

  /** Are we finished? */
  private boolean done = false;

  private PrintStream ps;

  /**
   * Creates a new <code>Report</code> that sends its output to a
   * given <code>PrintWriter</code>.  This is useful when you want to
   * dump to the console.  The "student's" id is
   * <code>"writer"</code>.
   */
  public Report(PrintWriter pw) {
    this.log = pw;
  }
  

  /**
   * Creates a new <code>Report</code> for a given student.  It will
   * create a log file in given directory.
   *
   * @param id
   *        The student's login id
   * @param logDir
   *        Directory into which the log file is placed.
   */
  public Report(final String id, File logDir) {
    if (!logDir.isDirectory()) {
      throw new IllegalArgumentException(logDir + 
					 " is not a directory");
    }

    if (!logDir.exists()) {
      logDir.mkdirs();
    }

    String logFileName = id + ".log";
    File logFile = new File(logDir, logFileName);
    

    // Create a PrintStream around the log file.  Set System.out and
    // System.err to refer to this guy.
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(logFile);

    } catch (IOException ex) {
      throw new IllegalArgumentException("Could not open " +
					 logFileName);
    }

    /**
     * Inner class used to prevent file stream from being closed until
     * I say so.  Other code may attempt to close System.out which
     * result in this file stream being closed. 
     */
    class CheckedPrintStream extends PrintStream {
      public CheckedPrintStream(OutputStream stream) {
	super(stream);
      }

      public void close() {
	if(Report.this.done) {
//  	  Report.this.consoleOut.println("Report " + id + 
//  					 " is closing stream");
	  super.close();
	}
	
//  	Report.this.consoleOut.println("Report " + id + 
//  				       " is not done yet");
      }
    }


//      this.ps = System.out;
    this.ps = new CheckedPrintStream(fos);
    System.setOut(ps);
    System.setErr(ps);

    // Create a PrintWriter around the PrintStream
    this.log = new PrintWriter(ps, true);
  }

  /**
   * Makes note of an exception 
   */
  public void logException(String msg, Exception ex) {
    println("** EXCEPTION " + msg);
    ex.printStackTrace(this.log);
  }
  
  /**
   * Flushes the stream that is being written to
   */
  public void flush() {
    this.log.flush();
  }

  /**
   * Prints a left justified line of text to the log file.
   */
  public void println(Object o) {
    this.log.println(o);
  }

  /**
   * Prints a left justified line of text to the log file.
   */
  public void println(String text) {
    this.log.println(text);
  }

  /**
   * Prints a line of text to the log file and centers it.
   */
  public void printlnCentered(String text) {
    printBanner(text, ' ');
  }

  /**
   * Prints a line of text to the log file, centers it, around
   * surrounds it with a given character.
   */
  public void printBanner(String text, char c) {
    int length = text.length();

    // Don't bother centering if the text is wider than the log.
    if (length < WIDTH) {
      int indent = (WIDTH - length) / 2;

      for (int i = 0; i < indent - 1; i++) {
	this.log.print(c);
      }
      this.log.print(' ');
      this.log.flush();
    }

    this.log.print(text);

    if (length < WIDTH) {
      int indent = (WIDTH - length) / 2;

      this.log.print(' ');
      for (int i = 0; i < indent - 1; i++) {
	this.log.print(c);
      }
      this.log.flush();
    }

    this.log.println("");
  }

  /**
   * Signifies that this <code>Report</code> is done
   */
  void done() {
    this.done = true;
  }

  /**
   * Make sure we're done when this <code>Report</code> gets garbage
   * collected.  This way, the stream will always get closed.
   */
  public void finalize() {
    this.done = true;
  }

}
