package edu.pdx.cs399J.family;

/**
 * This exception is thrown when something is wrong with a family tree
 * or a data source from which a family tree is read.
 */
public class FamilyTreeException extends RuntimeException {

  /** The exception being wrapped by this FamilyTreeException */
  private Throwable nested;

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
