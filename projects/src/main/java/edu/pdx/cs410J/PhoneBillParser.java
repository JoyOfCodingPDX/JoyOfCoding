package edu.pdx.cs410J;

/**
 * Classes that implement this interface read some source and from it
 * create a phone bill.
 */
public interface PhoneBillParser {

  /**
   * Parses some source and returns a phone bill
   *
   * @throws ParserException
   *         If the source cannot be parsed
   */
  public AbstractPhoneBill parse() throws ParserException;
  

}
