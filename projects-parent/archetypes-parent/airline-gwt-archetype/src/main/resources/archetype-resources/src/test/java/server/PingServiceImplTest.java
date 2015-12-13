#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.server;

import ${package}.client.Airline;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class PingServiceImplTest {

  @Test
  public void pingReturnsExpectedAirline() {
    PingServiceImpl service = new PingServiceImpl();
    Airline airline = service.ping();
    assertThat(airline.getFlights().size(), equalTo(1));
  }
}
