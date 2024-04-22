package edu.pdx.cs.joy;

import java.io.IOException;

/**
 * Classes that implement this interface dump the contents of an
 * airline to some destination.
 *
 * @author David Whitlock
 * @version $Revision: 1.3 $
 * @since Fall 2001
 */
public interface AirlineDumper<T extends AbstractAirline> {

  /**
   * Dumps an airline to some destination.
   *
   * @param airline
   *        The airline being written to a destination
   *
   * @throws IOException
   *         Something went wrong while writing the airline
   */
  public void dump(T airline) throws IOException;

}
