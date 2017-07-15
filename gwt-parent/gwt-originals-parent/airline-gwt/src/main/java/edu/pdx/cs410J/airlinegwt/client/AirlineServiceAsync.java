package edu.pdx.cs410J.airlinegwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The client-side interface to the airline service
 */
public interface AirlineServiceAsync {

  /**
   * Return the current date/time on the server
   */
  void getAirline(AsyncCallback<Airline> async);
}
