import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

#set($symbol_pound='#')
  #set($symbol_dollar='$')
  #set($symbol_escape='\' )
  package ${package};

/**
 * Unit tests for the {@link PhoneCall} class.
 *
 * You'll need to update these unit tests as you build out your program.
 */
public class PhoneCallTest {

  @Test
  public void getStartTimeStringNeedsToBeImplemented() {
    PhoneCall call = new PhoneCall();
    assertThrows(UnsupportedOperationException.class, call::getStartTimeString);
  }

  @Test
  public void initiallyAllPhoneCallsHaveTheSameCallee() {
    PhoneCall call = new PhoneCall();
    assertThat(call.getCallee(), containsString("not implemented"));
  }

  @Test
  public void forProject1ItIsOkayIfGetStartTimeReturnsNull() {
    PhoneCall call = new PhoneCall();
    assertThat(call.getStartTime(), is(nullValue()));
  }
  
}
