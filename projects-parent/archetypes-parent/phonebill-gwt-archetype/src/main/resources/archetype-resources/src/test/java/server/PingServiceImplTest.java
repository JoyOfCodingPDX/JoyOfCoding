#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.server;

import ${package}.client.PhoneBill;
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
