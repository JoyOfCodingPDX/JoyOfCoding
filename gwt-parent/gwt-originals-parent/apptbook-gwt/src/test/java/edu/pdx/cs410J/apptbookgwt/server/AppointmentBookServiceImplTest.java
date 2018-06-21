package edu.pdx.cs410J.apptbookgwt.server;

import edu.pdx.cs410J.apptbookgwt.client.AppointmentBook;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class AppointmentBookServiceImplTest {

  @Test
  public void getAppointmentBookReturnsExpectedAppointmentBook() {
    AppointmentBookServiceImpl service = new AppointmentBookServiceImpl();
    AppointmentBook airline = service.getAppointmentBook();
    assertThat(airline.getAppointments().size(), equalTo(1));
  }
}
