package edu.pdx.cs410J.apptbookgwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.pdx.cs410J.apptbookgwt.client.Appointment;
import edu.pdx.cs410J.apptbookgwt.client.AppointmentBook;
import edu.pdx.cs410J.apptbookgwt.client.AppointmentBookService;

/**
 * The server-side implementation of the division service
 */
public class AppointmentBookServiceImpl extends RemoteServiceServlet implements AppointmentBookService
{
  @Override
  public AppointmentBook getAppointmentBook() {
    AppointmentBook book = new AppointmentBook();
    book.addAppointment(new Appointment());
    return book;
  }

  @Override
  protected void doUnexpectedFailure(Throwable unhandled) {
    unhandled.printStackTrace(System.err);
    super.doUnexpectedFailure(unhandled);
  }

}
