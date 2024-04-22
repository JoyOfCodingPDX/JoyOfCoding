package edu.pdx.cs.joy;

/**
 * Classes that implement this interface read some source and from it
 * create an airline.
 */
public interface AirlineParser<T extends AbstractAirline> {
  /**
   * Parses some source and returns an airline.
   *
   * @throws ParserException
   *         If the source is malformatted.
   */
  public T parse() throws ParserException;
}
