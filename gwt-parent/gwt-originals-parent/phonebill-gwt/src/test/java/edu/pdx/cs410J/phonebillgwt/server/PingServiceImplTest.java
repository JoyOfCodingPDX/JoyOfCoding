package edu.pdx.cs410J.phonebillgwt.server;

import edu.pdx.cs410J.phonebillgwt.client.PhoneBill;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class PingServiceImplTest {

  @Test
  public void pingReturnsExpectedAirline() {
    PingServiceImpl service = new PingServiceImpl();
    PhoneBill airline = service.ping();
    assertThat(airline.getPhoneCalls().size(), equalTo(1));
  }
}
