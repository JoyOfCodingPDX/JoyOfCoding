#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import ${package}.client.PhoneBill;
import ${package}.client.PhoneCall;
import ${package}.client.PingService;

/**
 * The server-side implementation of the Phone Bill service
 */
public class PingServiceImpl extends RemoteServiceServlet implements PingService
{
  @Override
  public PhoneBill ping() {
    PhoneBill phonebill = new PhoneBill();
    phonebill.addPhoneCall(new PhoneCall());
    return phonebill;
  }

  /**
   * Log unhandled exceptions to standard error
   *
   * @param unhandled
   *        The exception that wasn't handled
   */
  @Override
  protected void doUnexpectedFailure(Throwable unhandled) {
    unhandled.printStackTrace(System.err);
    super.doUnexpectedFailure(unhandled);
  }
}
