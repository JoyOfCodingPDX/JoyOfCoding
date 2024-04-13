package edu.pdx.cs.joy.apptbook;

import edu.pdx.cs.joy.AbstractAppointmentBook;

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
