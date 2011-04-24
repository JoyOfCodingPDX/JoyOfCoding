package edu.pdx.cs410J;

import java.io.Serializable;
import java.util.Collection;

/**
 * This class represents an appointment book that holds multiple
 * appointments.  Each appointment book has an owner. 
 *
 * @author David Whitlock
 */
public abstract class AbstractAppointmentBook implements Serializable {

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
   * Adds an appointment to this appointment book
   */
  public abstract void addAppointment(AbstractAppointment appt);

  /**
   * Returns a brief textual description of this appointment book
   */
  public String toString() {
    return this.getOwnerName() + "'s appointment book with " +
      this.getAppointments().size() + " appointments";
  }
}
