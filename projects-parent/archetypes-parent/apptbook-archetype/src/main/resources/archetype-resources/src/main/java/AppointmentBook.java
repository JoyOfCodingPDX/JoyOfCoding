#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import edu.pdx.cs410J.AbstractAppointmentBook;

import java.util.Collection;

public class AppointmentBook extends AbstractAppointmentBook<Appointment> {
  private final String owner;

  public AppointmentBook(String owner) {
    this.owner = owner;
  }

  @Override
  public String getOwnerName() {
    return this.owner;
  }

  @Override
  public Collection<Appointment> getAppointments() {
    throw new UnsupportedOperationException("This method is not implemented yet");
  }

  @Override
  public void addAppointment(Appointment appt) {
    throw new UnsupportedOperationException("This method is not implemented yet");
  }
}
