package edu.pdx.cs410J.airlinegwt.server;

import edu.pdx.cs410J.airlinegwt.client.Airline;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class AirlineServiceImplTest {

  @Test
  public void airlineServiceReturnsExpectedAirline() {
    AirlineServiceImpl service = new AirlineServiceImpl();
    Airline airline = service.getAirline();
    assertThat(airline.getFlights().size(), equalTo(1));
  }

  @Test
  public void airlineServiceAlwaysThrowsAnUndeclaredException() {
    AirlineServiceImpl service = new AirlineServiceImpl();
    try {
      service.throwUndeclaredException();
      fail("Should have thrown an exception");

    } catch (IllegalStateException ex) {
      assertThat(ex.getMessage(), equalTo("Expected undeclared exception"));
    }
  }

  @Test
  public void airlineServiceAlwaysThrowsADeclaredException() {
    AirlineServiceImpl service = new AirlineServiceImpl();
    try {
      service.throwDeclaredException();
      fail("Should have thrown an exception");

    } catch (IllegalStateException ex) {
      assertThat(ex.getMessage(), equalTo("Expected declared exception"));
    }
  }

}
