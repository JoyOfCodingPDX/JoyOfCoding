package edu.pdx.cs410J.airline;

import edu.pdx.cs410J.AirlineDumper;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * A skeletal implementation of the <code>TextDumper</code> class for Project 2.
 */
public class TextDumper implements AirlineDumper<Airline> {
  private final Writer writer;

  public TextDumper(Writer writer) {
    this.writer = writer;
  }

  @Override
  public void dump(Airline airline) {
    try (
      PrintWriter pw = new PrintWriter(this.writer)
      ) {
      pw.println(airline.getName());

      pw.flush();
    }
  }
}
