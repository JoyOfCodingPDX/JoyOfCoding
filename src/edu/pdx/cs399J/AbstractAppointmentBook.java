package edu.pdx.cs410J;

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
   * Returns all of the appointments in this appointment book.
   */
  public abstract Collection getAppointments();

}
