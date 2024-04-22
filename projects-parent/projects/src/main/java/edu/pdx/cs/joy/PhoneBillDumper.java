package edu.pdx.cs.joy;

import java.io.IOException;

/**
 * Classes that implement this interface dump the contents of a phone
 * bill to some destination.
 *
 * @author David Whitlock
 * @version $Revision: 1.3 $
 * @since Spring 2001
 */
public interface PhoneBillDumper<T extends AbstractPhoneBill> {

  /**
   * Dumps a phone bill to some destination.
   */
  public void dump(T bill) throws IOException;

}
