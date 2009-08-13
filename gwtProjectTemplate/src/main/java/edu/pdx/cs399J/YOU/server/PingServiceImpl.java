package edu.pdx.cs399J.YOU.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.pdx.cs399J.AbstractPhoneBill;
import edu.pdx.cs399J.YOU.client.PhoneBill;
import edu.pdx.cs399J.YOU.client.PhoneCall;
import edu.pdx.cs399J.YOU.client.PingService;

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
