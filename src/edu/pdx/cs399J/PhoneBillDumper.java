package edu.pdx.cs399J;

/**
 * Classes that implement this interface dump the contents of a phone
 * bill to some destination.
 *
 * @author David Whitlock
 * @version $Revision: 1.2 $
 * @since Spring 2001
 */
public interface PhoneBillDumper {

  /**
   * Dumps a phone bill to some destination.
   */
  public void dump(AbstractPhoneBill bill);

}
