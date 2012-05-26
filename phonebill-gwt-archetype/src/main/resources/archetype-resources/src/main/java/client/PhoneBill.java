#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client;

import edu.pdx.cs410J.AbstractPhoneCall;
import edu.pdx.cs410J.AbstractPhoneBill;

import java.lang.Override;
import java.util.ArrayList;
import java.util.Collection;

public class PhoneBill extends AbstractPhoneBill
{
  private Collection<AbstractPhoneCall> calls = new ArrayList<AbstractPhoneCall>();

  @Override
  public String getCustomer() {
    return "CS410J";
  }

  @Override
  public void addPhoneCall(AbstractPhoneCall call) {
    this.calls.add(call);
  }

  @Override
  public Collection getPhoneCalls() {
    return this.calls;
  }
}
