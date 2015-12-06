package edu.pdx.cs410J;

import java.io.Serializable;
import java.util.Date;

/**
 * This abstract class represents a phone call between a caller (the
 * phone number of the person who originates the call) and callee (the
 * phone number of the person whose receives the phone call).  Phone
 * calls begin and end at given times.
 *
 * @author David Whitlock
 */
public abstract class AbstractPhoneCall implements Serializable {

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
   * {@link Date}. 
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
   * {@link Date}.
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
   * Returns a brief textual description of this phone call.
   */
  public String toString() {
    return "Phone call from " + this.getCaller() + " to " +
      this.getCallee() + " from " + this.getStartTimeString() + 
      " to " + this.getEndTimeString();
  }

}
