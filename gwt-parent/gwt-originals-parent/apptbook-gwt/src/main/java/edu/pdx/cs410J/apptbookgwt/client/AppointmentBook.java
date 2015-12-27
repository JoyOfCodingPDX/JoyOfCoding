package edu.pdx.cs410J.apptbookgwt.client;

import edu.pdx.cs410J.AbstractAppointment;
import edu.pdx.cs410J.AbstractAppointmentBook;

import java.util.ArrayList;
import java.util.Collection;

public class AppointmentBook extends AbstractAppointmentBook
{

    private Collection<AbstractAppointment> appts = new ArrayList<AbstractAppointment>();

    @Override
    public String getOwnerName()
    {
        return "My Owner";
    }

    @Override
    public Collection getAppointments()
    {
        return this.appts;
    }

    @Override
    public void addAppointment( AbstractAppointment appt )
    {
        this.appts.add(appt);
    }
}
