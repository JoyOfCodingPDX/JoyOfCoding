package edu.pdx.cs410J;

/**
 * Classes that implement this interface read some source and from it
 * create an airline.
 */
public interface AirlineParser {
  /**
   * Parses some source and returns an airline.
   *
   * @throws ParserException
   *         If the source is malformatted.
   */
  public AbstractAirline parse() throws ParserException; 
}
