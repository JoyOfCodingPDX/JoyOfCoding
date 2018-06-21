package edu.pdx.cs410J.apptbookgwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The client-side interface to the Appointment Book service
 */
public interface AppointmentBookServiceAsync {

  /**
   * Return the current date/time on the server
   */
  void getAppointmentBook(AsyncCallback<AppointmentBook> async);

  /**
   * Always throws an exception so that we can see how to handle uncaught
   * exceptions in GWT.
   */
  void throwUndeclaredException(AsyncCallback<Void> async);

  /**
   * Always throws a declared exception so that we can see GWT handles it.
   */
  void throwDeclaredException(AsyncCallback<Void> async);

}
