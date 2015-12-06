package edu.pdx.cs410J;

/**
 * This interface is to be implemented by classes that read some
 * source and from it create an appointment book.
 *
 * @author David Whitlock
 */
public interface AppointmentBookParser {

  /**
   * Parses the contents of a file or other input source and returns
   * an appointment book.
   */
  public AbstractAppointmentBook parse() throws ParserException;

}
