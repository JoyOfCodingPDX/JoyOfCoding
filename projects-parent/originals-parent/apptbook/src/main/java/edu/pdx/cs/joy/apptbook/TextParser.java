package edu.pdx.cs.joy.apptbook;

import edu.pdx.cs.joy.AppointmentBookParser;
import edu.pdx.cs.joy.ParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * A skeletal implementation of the <code>TextParser</code> class for Project 2.
 */
public class TextParser implements AppointmentBookParser<AppointmentBook> {
  private final Reader reader;

  public TextParser(Reader reader) {
    this.reader = reader;
  }

  @Override
  public AppointmentBook parse() throws ParserException {
    try (
      BufferedReader br = new BufferedReader(this.reader)
    ) {

      String owner = br.readLine();

      if (owner == null) {
        throw new ParserException("Missing owner");
      }

      return new AppointmentBook(owner);

    } catch (IOException e) {
      throw new ParserException("While parsing appointment book text", e);
    }
  }
}
