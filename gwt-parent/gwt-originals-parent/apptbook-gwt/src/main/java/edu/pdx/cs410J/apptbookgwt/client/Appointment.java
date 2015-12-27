package edu.pdx.cs410J.apptbookgwt.client;

import edu.pdx.cs410J.AbstractAppointment;

import java.util.Date;

public class Appointment extends AbstractAppointment
{
    @Override
    public String getBeginTimeString()
    {
        return "START " + getBeginTime();
    }

    @Override
    public String getEndTimeString()
    {
        return "END + " + getEndTime();
    }

    @Override
    public Date getEndTime()
    {
        return new Date();
    }

    @Override
    public String getDescription()
    {
        return "My description";
    }

    @Override
    public Date getBeginTime()
    {
        return new Date();
    }
}
