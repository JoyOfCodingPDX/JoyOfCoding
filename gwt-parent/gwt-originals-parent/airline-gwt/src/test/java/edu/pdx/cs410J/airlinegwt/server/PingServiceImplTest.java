package edu.pdx.cs410J.airlinegwt.server;

import edu.pdx.cs410J.airlinegwt.client.Airline;
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
