package edu.pdx.cs399J;

/**
 * Classes that implement this interface dump the contents of a phone
 * bill to some destination.
 */
public interface PhoneBillDumper {

  /**
   * Dumps a phone bill to some destination.
   */
  public void dump(AbstractPhoneBill bill);

}
