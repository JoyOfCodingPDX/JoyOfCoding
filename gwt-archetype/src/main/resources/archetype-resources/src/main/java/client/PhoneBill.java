#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client;

import edu.pdx.cs399J.AbstractPhoneBill;
import edu.pdx.cs399J.AbstractPhoneCall;

import java.util.ArrayList;
import java.util.Collection;

public class PhoneBill extends AbstractPhoneBill
{

    private Collection<AbstractPhoneCall> calls = new ArrayList<AbstractPhoneCall>();

    @Override
    public String getCustomer()
    {
        return "My Customer";
    }

    @Override
    public void addPhoneCall( AbstractPhoneCall call )
    {
        calls.add(call);
    }

    @Override
    public Collection getPhoneCalls()
    {
        return calls;
    }
}
