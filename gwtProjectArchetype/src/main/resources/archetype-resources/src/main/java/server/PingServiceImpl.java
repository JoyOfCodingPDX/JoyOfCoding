#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.pdx.cs399J.AbstractPhoneBill;
import ${package}.client.PhoneBill;
import ${package}.client.PhoneCall;
import ${package}.client.PingService;

/**
 * The server-side implementation of the division service
 */
public class PingServiceImpl extends RemoteServiceServlet implements PingService
{
    public AbstractPhoneBill ping()
    {
        PhoneBill bill = new PhoneBill();
        bill.addPhoneCall( new PhoneCall() );
        return bill;
    }
}
