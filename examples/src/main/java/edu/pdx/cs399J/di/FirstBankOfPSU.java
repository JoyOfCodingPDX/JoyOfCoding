package edu.pdx.cs399J.di;

/**
 * A {@link CreditCardService} that makes REST calls to access the First Bank of Portland State.
 */
public class FirstBankOfPSU implements CreditCardService
{
    private final String serverHost;

    private final int serverPort;

    public FirstBankOfPSU( String serverHost, int serverPort )
    {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public CreditTransactionCode debit( CreditCard card, double amount )
    {
        throw new UnsupportedOperationException( "This method is not implemented yet" );
    }
}
