package edu.pdx.cs410J;

import java.io.Serializable;
import java.util.Date;

/**
 * This class represents an airline flight.  Each flight has a unique
 * number identifying it, an origin airport identified by the
 * airport's three-letter code, a departure time, a destination
 * airport identified by the airport's three-letter code, and an
 * arrival time.
 */
public abstract class AbstractFlight implements Serializable {

  /**
   * Returns a number that uniquely identifies this flight.
   */
  public abstract int getNumber();

  /**
   * Returns the three-letter code of the airport at which this flight
   * originates.
   */
  public abstract String getSource();

  /**
   * Returns this flight's departure time as a <code>Date</code>.
   */
  public Date getDeparture() {
    return null;
  }

  /**
   * Returns a textual representation of this flight's departure
   * time.
   */
  public abstract String getDepartureString();

  /**
   * Returns the three-letter code of the airport at which this flight
   * terminates.
   */
  public abstract String getDestination();

  /**
   * Returns this flight's arrival time as a <code>Date</code>.
   */
  public Date getArrival() {
    return null;
  }

  /**
   * Returns a textual representation of this flight's arrival time.
   */
  public abstract String getArrivalString();

  /**
   * Returns a brief textual description of this flight.
   */
  public String toString() {
    return "Flight " + this.getNumber() + " departs " + this.getSource()
           + " at " + this.getDepartureString() + " arrives " +
           this.getDestination() + " at " + this.getArrivalString();
  }
}
