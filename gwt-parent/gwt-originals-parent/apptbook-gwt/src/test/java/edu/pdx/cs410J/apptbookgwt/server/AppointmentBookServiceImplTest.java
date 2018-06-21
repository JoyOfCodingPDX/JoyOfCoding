package edu.pdx.cs410J.apptbookgwt.server;

import edu.pdx.cs410J.apptbookgwt.client.AppointmentBook;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class AppointmentBookServiceImplTest {

  @Test
  public void getAppointmentBookReturnsExpectedAppointmentBook() {
    AppointmentBookServiceImpl service = new AppointmentBookServiceImpl();
    AppointmentBook book = service.getAppointmentBook();
    assertThat(book.getAppointments().size(), equalTo(1));
  }
}
