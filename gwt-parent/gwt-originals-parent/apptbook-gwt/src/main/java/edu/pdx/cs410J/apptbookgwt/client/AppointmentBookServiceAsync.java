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
}
