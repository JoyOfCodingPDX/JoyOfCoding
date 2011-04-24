package edu.pdx.cs410J.di;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * A {@link CreditCardService} that makes REST calls to access the First Bank of Portland State.
 */
public class FirstBankOfPSU implements CreditCardService
{
    private final String serverHost;

    private final int serverPort;

    @Inject
    public FirstBankOfPSU( @Named("ServerHost") String serverHost, @Named("ServerPort") int serverPort )
    {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public CreditTransactionCode debit( CreditCard card, double amount )
    {
        throw new UnsupportedOperationException( "This method is not implemented yet" );
    }
}
