package edu.pdx.cs399J;

import java.util.*;

/**
 * This class represents an appointment book that holds multiple
 * appointments.  Each appointment book has an owner. 
 *
 * @author David Whitlock
 */
public abstract class AbstractAppointmentBook {

  /**
   * Returns the name of the owner of this appointment book.
   */
  public abstract String getOwnerName();

  /**
   * Returns all of the appointments in this appointment book as a
   * collection of {@link AbstractAppointment}s.
   */
  public abstract Collection getAppointments();

  /**
   * Returns a brief textual description of this appointment book
   */
  public String toString() {
    return this.getOwnerName() + "'s appointment book with " +
      this.getAppointments().size() + " appointments";
  }
}
