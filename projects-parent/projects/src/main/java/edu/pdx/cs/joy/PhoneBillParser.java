package edu.pdx.cs.joy;

/**
 * Classes that implement this interface read some source and from it
 * create a phone bill.
 */
public interface PhoneBillParser<T extends AbstractPhoneBill> {

  /**
   * Parses some source and returns a phone bill
   *
   * @throws ParserException
   *         If the source cannot be parsed
   */
  public T parse() throws ParserException;
  

}
