package edu.pdx.cs410J;

import java.io.Serializable;
import java.util.Collection;

/**
 * This abstract class represents a customer's phone bill that
 * consists of multiple phone calls.
 *
 * @author David Whitlock
 */
public abstract class AbstractPhoneBill<T extends AbstractPhoneCall> implements Serializable {

  /**
   * Returns the name of the customer whose phone bill this is
   */
  public abstract String getCustomer();

  /**
   * Adds a phone call to this phone bill
   */
  public abstract void addPhoneCall(T call);

  /**
   * Returns all of the phone calls (as instances of {@link
   * AbstractPhoneCall}) in this phone bill
   */
  public abstract Collection<T> getPhoneCalls();

  /**
   * Returns a brief textual description of this phone bill
   */
  public String toString() {
    return this.getCustomer() + "'s phone bill with " +
      this.getPhoneCalls().size() + " phone calls";
  }

}
