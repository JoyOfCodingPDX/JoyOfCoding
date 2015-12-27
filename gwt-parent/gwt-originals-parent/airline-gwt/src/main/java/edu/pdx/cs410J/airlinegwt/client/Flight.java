package edu.pdx.cs410J.airlinegwt.client;

import edu.pdx.cs410J.AbstractFlight;

import java.util.Date;

public class Flight extends AbstractFlight
{
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
