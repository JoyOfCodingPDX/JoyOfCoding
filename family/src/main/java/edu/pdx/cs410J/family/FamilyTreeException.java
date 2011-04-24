package edu.pdx.cs410J.family;

/**
 * This exception is thrown when something is wrong with a family tree
 * or a data source from which a family tree is read.
 */
@SuppressWarnings("serial")
public class FamilyTreeException extends RuntimeException {

  ///////////////////////  Constructors  ///////////////////////

  /**
   * Creates a new <Code>FamilyTreeException</code> with the given
   * message.
   */
  public FamilyTreeException(String message) {
    super(message);
  }

  /**
   * Creates a new <code>FamilyTreeException</code> that wraps another
   * exception.
   */
  public FamilyTreeException(String message, Throwable nested) {
    super(message, nested);
  }

}
