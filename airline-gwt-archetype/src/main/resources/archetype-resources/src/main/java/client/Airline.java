#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client;

import edu.pdx.cs410J.AbstractFlight;
import edu.pdx.cs410J.AbstractAirline;

import java.util.ArrayList;
import java.util.Collection;

public class Airline extends AbstractAirline
{
  private Collection<AbstractFlight> flights = new ArrayList<AbstractFlight>();

  public String getName() {
    return "Air CS410J";
  }

  public void addFlight(AbstractFlight flight) {
    this.flights.add(flight);
  }

  public Collection getFlights() {
    return this.flights;
  }
}
