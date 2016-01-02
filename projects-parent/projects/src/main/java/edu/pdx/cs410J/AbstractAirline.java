package edu.pdx.cs410J;

import java.io.Serializable;
import java.util.Collection;

/**
 * This class represents an airline.  Each airline has a name and
 * consists of multiple flights.
 */
public abstract class AbstractAirline<T extends AbstractFlight> implements Serializable
{

  /**
   * Returns the name of this airline.
   */
  public abstract String getName();

  /**
   * Adds a flight to this airline.
   */
  public abstract void addFlight(T flight);

  /**
   * Returns all of this airline's flights.
   */
  public abstract Collection<T> getFlights();

  /**
   * Returns a brief textual description of this airline.
   */
  public String toString() {
    return this.getName() + " with " + this.getFlights().size() + 
           " flights";
  }

}
