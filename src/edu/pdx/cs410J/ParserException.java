package edu.pdx.cs410J;

/**
 * A <tt>ParserException</tt> is thrown when a file or other data
 * source is being parsed and it is decided that the source is
 * malformatted.
 *
 * @author David Whitlock
 */
public class ParserException extends Exception {

  /**
   * Constructor.
   */
  public ParserException(String description) {
    super(description);
  }

}
