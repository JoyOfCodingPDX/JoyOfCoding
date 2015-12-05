package edu.pdx.cs410J;

/**
 * A <code>ParserException</code> is thrown when a file or other data
 * source is being parsed and it is decided that the source is
 * malformatted.
 *
 * @author David Whitlock
 */
@SuppressWarnings("serial")
public class ParserException extends Exception {

  /**
   * Creates a new <code>ParserException</code> with a given
   * descriptive message.
   */
  public ParserException(String description) {
    super(description);
  }

  /**
   * Creates a new <code>ParserException</code> that was caused by
   * another exception.
   */
  public ParserException(String description, Throwable cause) {
    super(description, cause);
  }

}
