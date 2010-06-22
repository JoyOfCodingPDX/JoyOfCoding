#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client;

import edu.pdx.cs399J.AbstractPhoneCall;

import java.util.Date;

public class PhoneCall extends AbstractPhoneCall
{
    @Override
    public String getCaller()
    {
        return "My Caller";
    }

    @Override
    public String getCallee()
    {
        return "My Callee";
    }

    @Override
    public String getStartTimeString()
    {
        return "START " + getStartTime();
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
    public Date getStartTime()
    {
        return new Date();
    }
}
