package edu.pdx.cs399J.YOU.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.pdx.cs399J.YOU.client.PingService;

import java.util.Date;

/**
 * The server-side implementation of the division service
 */
public class PingServiceImpl extends RemoteServiceServlet implements PingService
{
    public Date ping()
    {
        return new Date();
    }
}
