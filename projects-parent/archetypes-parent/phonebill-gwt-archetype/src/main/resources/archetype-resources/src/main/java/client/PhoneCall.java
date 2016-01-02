#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client;

import edu.pdx.cs410J.AbstractPhoneCall;

import java.lang.Override;
import java.util.Date;

public class PhoneCall extends AbstractPhoneCall
{

  @Override
  public String getCaller() {
    return "123-345-6789";
  }

  @Override
  public Date getStartTime() {
    return new Date();
  }

  @Override
  public String getStartTimeString() {
    return "START " + getStartTime();
  }

  @Override
  public String getCallee() {
    return "345-677-2341";
  }

  @Override
  public Date getEndTime() {
    return new Date();
  }

  @Override
  public String getEndTimeString() {
    return "END " + getEndTime();
  }

}
