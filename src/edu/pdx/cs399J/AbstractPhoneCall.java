package edu.pdx.cs399J;

import java.util.Date;

/**
 * This abstract class represents a phone call between a caller (the
 * phone number of the person who originates the call) and callee (the
 * phone number of the person whose receives the phone call).  Phone
 * calls begin and end at given times.
 */
public abstract class AbstractPhoneCall {

  /**
   * Returns the phone number of the person who originated this phone
   * call.
   */
  public abstract String getCaller();

  /**
   * Returns the phone number of the person who received this phone
   * call.
   */
  public abstract String getCallee();

  /**
   * Returns the time that this phone call was originated as a
   * <code>Date</code>. 
   */
  public Date getStartTime() {
    return null;
  }

  /**
   * Returns a textual representation of the time that this phone call
   * was originated.
   */
  public abstract String getStartTimeString();

  /**
   * Returns the time that this phone call was completed as a
   * <code>Date</code>.
   */
  public Date getEndTime() {
    return null;
  }

  /**
   * Returns a textual representation of the time that this phone call
   * was completed.
   */
  public abstract String getEndTimeString();

  /**
   * Returns a bried textual description of this phone call.
   */
  public String toString() {
    return "Phone call from " + this.getCaller() + " to " +
      this.getCallee() + " from " + this.getStartTimeString() + 
      " to " + this.getEndTimeString();
  }

}
