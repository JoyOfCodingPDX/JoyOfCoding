package edu.pdx.cs399J;

/**
 * This interface is to be implemented by classes that create an
 * appointment book from the contents of a file.
 *
 * @author David Whitlock
 */
public interface Parser {

  /**
   * Parses the contents of a file or other input source and returns
   * an appointment book.
   */
  public AbstractAppointmentBook parse() throws ParserException;

}
