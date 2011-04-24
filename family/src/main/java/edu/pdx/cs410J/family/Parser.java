package edu.pdx.cs410J.family;

/**
 * Classes that implement this interface create a family tree from
 * some source.
 */
public interface Parser {

  /**
   * Creates a family tree from the data stored in a source.
   *
   * @throws FamilyTreeException
   *         The data source is malformatted
   */
  public FamilyTree parse() throws FamilyTreeException;

}
