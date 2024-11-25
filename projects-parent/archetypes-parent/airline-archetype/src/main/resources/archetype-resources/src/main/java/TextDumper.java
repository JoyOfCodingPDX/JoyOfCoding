#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import edu.pdx.cs.joy.AirlineDumper;

import java.io.IOException;
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
  public void dump(Airline ${artifactId}) {
    try (
      PrintWriter pw = new PrintWriter(this.writer)
      ) {
      pw.println(${artifactId}.getName());

      pw.flush();
    }
  }
}
