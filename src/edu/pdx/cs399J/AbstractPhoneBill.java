package edu.pdx.cs399J;

import java.util.Collection;

/**
 * This abstract class represents a customer's phone bill that
 * consists of multiple phone calls.
 */
public abstract class AbstractPhoneBill {

  /**
   * Returns the name of the customer whose phone bill this is
   */
  public abstract String getCustomer();

  /**
   * Adds a phone call to this phone bill
   */
  public abstract void addPhoneCall(AbstractPhoneCall call);

  /**
   * Returns all of the phone calls (as instances of {@link
   * AbstractPhoneCall}) in this phone bill
   */
  public abstract Collection getPhoneCalls();

  /**
   * Returns a brief textual description of this phone bill
   */
  public String toString() {
    return this.getCustomer() + "'s phone bill with " +
      this.getPhoneCalls().size() + " phone calls";
  }

}
