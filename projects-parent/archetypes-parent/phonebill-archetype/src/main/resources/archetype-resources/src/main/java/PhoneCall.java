#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import edu.pdx.cs.joy.AbstractPhoneCall;

public class PhoneCall extends AbstractPhoneCall {
  @Override
  public String getCaller() {
    throw new UnsupportedOperationException("This method is not implemented yet");
  }

  @Override
  public String getCallee() {
    return "This method is not implemented yet";
  }

  @Override
  public String getBeginTimeString() {
    throw new UnsupportedOperationException("This method is not implemented yet");
  }

  @Override
  public String getEndTimeString() {
    throw new UnsupportedOperationException("This method is not implemented yet");
  }
}
