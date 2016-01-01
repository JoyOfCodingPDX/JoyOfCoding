package edu.pdx.cs410J.airlinegwt.client;

import edu.pdx.cs410J.AbstractFlight;
import edu.pdx.cs410J.AbstractAirline;

import java.util.ArrayList;
import java.util.Collection;

public class Airline extends AbstractAirline
{
  private Collection<AbstractFlight> flights = new ArrayList<AbstractFlight>();

  @Override
  public String getName() {
    return "Air CS410J";
  }

  @Override
  public void addFlight(AbstractFlight flight) {
    this.flights.add(flight);
  }

  @Override
  public Collection getFlights() {
    return this.flights;
  }
}
