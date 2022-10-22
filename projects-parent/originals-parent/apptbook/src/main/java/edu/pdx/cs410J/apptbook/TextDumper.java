package edu.pdx.cs410J.apptbook;

import edu.pdx.cs410J.AppointmentBookDumper;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * A skeletal implementation of the <code>TextDumper</code> class for Project 2.
 */
public class TextDumper implements AppointmentBookDumper<AppointmentBook> {
  private final Writer writer;

  public TextDumper(Writer writer) {
    this.writer = writer;
  }

  @Override
  public void dump(AppointmentBook book) {
    try (
      PrintWriter pw = new PrintWriter(this.writer)
    ) {
      pw.println(book.getOwnerName());

      pw.flush();
    }

  }
}
