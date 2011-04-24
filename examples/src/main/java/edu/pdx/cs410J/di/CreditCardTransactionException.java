package edu.pdx.cs410J.di;

/**
 * Throw when there was an error when transacting with a {@link edu.pdx.cs410J.di.CreditCardService}
 */
public class CreditCardTransactionException extends RuntimeException
{
    private CreditTransactionCode code;

    public CreditCardTransactionException( CreditTransactionCode code )
    {
        super(code.name());
        this.code = code;
    }

    /**
     * Returns the error code of the transaction 
     * @return
     */
    public CreditTransactionCode getCode()
    {
        return code;
    }
}
