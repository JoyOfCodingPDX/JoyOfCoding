package edu.pdx.cs410J.airlinegwt.server;

import edu.pdx.cs410J.airlinegwt.client.Airline;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class AirlineServiceImplTest {

  @Test
  public void airlineServiceReturnsExpectedAirline() {
    AirlineServiceImpl service = new AirlineServiceImpl();
    Airline airline = service.getAirline();
    assertThat(airline.getFlights().size(), equalTo(1));
  }
}
