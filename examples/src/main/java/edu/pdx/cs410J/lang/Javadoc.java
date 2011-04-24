package edu.pdx.cs410J.lang;

/**
 * <P>This class demonstrates Javadoc comments</P>
 *
 * @author David Whitlock
 * @version 1.0
 */
public class Javadoc {

  /**
   * Returns the inverse of a <code>double</code>
   * @param d
   *        The <code>double</code> to invert
   * @return The inverse of a <code>double</code>
   * @throws IllegalArgumentException
   *         The <code>double</code> is zero
   */
  public double invert(double d) 
    throws IllegalArgumentException {
    if (d == 0.0) {
      String s = d + " can't be zero!";
      throw new IllegalArgumentException(s);
    } else {
      return 1.0 / d;
    }
  }
}
