#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client;

import edu.pdx.cs399J.AbstractAppointment;
import edu.pdx.cs399J.AbstractAppointmentBook;

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
