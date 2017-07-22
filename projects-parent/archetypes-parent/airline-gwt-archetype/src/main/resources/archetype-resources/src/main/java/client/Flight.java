#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client;

import edu.pdx.cs410J.AbstractFlight;

import java.util.Date;

public class Flight extends AbstractFlight
{
  /**
   * In order for GWT to serialize this class (so that it can be sent between
   * the client and the server), it must have a zero-argument constructor.
   */
  public Flight() {

  }

  @Override
  public int getNumber() {
    return 42;
  }

  @Override
  public String getSource() {
    return "PDX";
  }

  @Override
  public Date getDeparture() {
    return new Date();
  }

  public String getDepartureString() {
    return "DEPART " + getDeparture();
  }

  public String getDestination() {
    return "MHT";
  }

  public Date getArrival() {
    return new Date();
  }

  public String getArrivalString() {
    return "ARRIVE " + getArrival();
  }

}
