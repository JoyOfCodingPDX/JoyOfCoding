package edu.pdx.cs410J.airlinegwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.pdx.cs410J.airlinegwt.client.Airline;
import edu.pdx.cs410J.airlinegwt.client.Flight;
import edu.pdx.cs410J.airlinegwt.client.AirlineService;

/**
 * The server-side implementation of the Airline service
 */
public class AirlineServiceImpl extends RemoteServiceServlet implements AirlineService
{
  @Override
  public Airline getAirline() {
    Airline airline = new Airline();
    airline.addFlight(new Flight());
    return airline;
  }

  @Override
  public void throwUndeclaredException() {
    throw new IllegalStateException("Expected undeclared exception");
  }

  @Override
  public void throwDeclaredException() throws IllegalStateException {
    throw new IllegalStateException("Expected declared exception");
  }

  /**
   * Log unhandled exceptions to standard error
   *
   * @param unhandled
   *        The exception that wasn't handled
   */
  @Override
  protected void doUnexpectedFailure(Throwable unhandled) {
    unhandled.printStackTrace(System.err);
    super.doUnexpectedFailure(unhandled);
  }
}
