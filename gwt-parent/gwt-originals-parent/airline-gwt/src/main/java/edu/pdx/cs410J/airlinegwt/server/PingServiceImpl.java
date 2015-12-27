package edu.pdx.cs410J.airlinegwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.pdx.cs410J.airlinegwt.client.Airline;
import edu.pdx.cs410J.airlinegwt.client.Flight;
import edu.pdx.cs410J.airlinegwt.client.PingService;

/**
 * The server-side implementation of the Airline service
 */
public class PingServiceImpl extends RemoteServiceServlet implements PingService
{
  @Override
  public Airline ping() {
    Airline airline = new Airline();
    airline.addFlight(new Flight());
    return airline;
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
