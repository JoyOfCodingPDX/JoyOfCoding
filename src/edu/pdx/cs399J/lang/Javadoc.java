package edu.pdx.cs399J.lang;

/**
 * <P>This class demonstrates Javadoc comments</P>
 *
 * <P align="center"><EM><A href =
 * "{@docRoot}/../src/edu/pdx/cs399J/lang/Javadoc.java">
 * View Source</A></EM></P>
 *
 * @author David Whitlock
 * @version 1.0
 */
public class Javadoc {

  /**
   * Returns the inverse of a <code>double</code>
   * @param f
   *        The <code>double</code> to invert
   * @return The inverse of a <code>double</code>
   * @throws IllegalArgumentException
   *         The <code>double</code> is zero
   */
  double invert(double d) 
    throws IllegalArgumentException {
    if (d == 0.0) {
      String s = d + " can't be zero!";
      throw new IllegalArgumentException(s);
    } else {
      return 1.0 / d;
    }
  }
}
