package edu.pdx.cs399J.YOU.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import edu.pdx.cs399J.AbstractPhoneBill;

/**
 * A GWT remote service that returns the date on the server
 */
@RemoteServiceRelativePath("ping")
public interface PingService extends RemoteService {

  /**
   * Returns the current date and time on the server
   */
  public AbstractPhoneBill ping();

}
