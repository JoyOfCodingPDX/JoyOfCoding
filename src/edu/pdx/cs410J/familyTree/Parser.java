package edu.pdx.cs410J.familyTree;

import edu.pdx.cs410J.ParserException;

/**
 * Classes that implement this interface create a family tree from
 * some source.
 */
public interface Parser {

  /**
   * Creates a family tree from the data stored in a source.
   *
   * @throws ParserException
   *         The data source is malformatted
   */
  public FamilyTree parse() throws ParserException;

}
