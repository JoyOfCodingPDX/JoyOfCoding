package edu.pdx.cs410J;

import java.util.*;

/**
 * This class represents an appointment found in an appointment book.
 * Each appointment has a starting and ending time, as well as a text
 * message describing itself.
 *
 * @author David Whitlock
 */
public abstract class AbstractAppointment {

  /**
   * Returns a String describing the beginning date and time of this
   * appointment.
   */
  public abstract String getBeginString();

  /**
   * Returns a String describing the ending date and time of this
   * appointment.
   */
  public abstract String getEndString();

  /**
   * Returns the Date that this appointment begins.
   */
  public Date begin() {
    return null;
  }

  /**
   * Returns the Date that this appointment ends.
   */
  public Date end() {
    return null;
  }

  /**
   * Returns a description of the appointment.
   */
  public abstract String text();

  public String toString() {
    return "You should override the toString() method of AbstractAppointment";
  }

}
