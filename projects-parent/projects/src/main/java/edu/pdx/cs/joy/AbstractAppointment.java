package edu.pdx.cs.joy;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * This class represents an appointment found in an appointment book.
 * Each appointment has a beginning and ending time, as well as a text
 * message describing itself.
 *
 * @author David Whitlock
 */
public abstract class AbstractAppointment implements Serializable
{

  /**
   * Returns a String describing the beginning date and time of this
   * appointment.
   */
  public abstract String getBeginTimeString();

  /**
   * Returns a String describing the ending date and time of this
   * appointment.
   */
  public abstract String getEndTimeString();

  /**
   * Returns the {@link Date} that this appointment begins.
   */
  public ZonedDateTime getBeginTime() {
    return null;
  }

  /**
   * Returns the {@link Date} that this appointment ends.
   */
  public ZonedDateTime getEndTime() {
    return null;
  }

  /**
   * Returns a description of this appointment (for instance,
   * <code>"Have coffee with Marsha"</code>).
   */
  public abstract String getDescription();

  /**
   * Returns a brief textual summary of this appointment.
   */
  public final String toString() {
    return this.getDescription() + " from " +
      this.getBeginTimeString() + " until " + this.getEndTimeString();
  }

}
