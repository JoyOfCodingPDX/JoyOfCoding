#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import ${package}.client.Airline;
import ${package}.client.Flight;
import ${package}.client.PingService;

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
