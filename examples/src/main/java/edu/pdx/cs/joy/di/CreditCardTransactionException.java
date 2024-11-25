package edu.pdx.cs.joy.di;

/**
 * Throw when there was an error when transacting with a {@link CreditCardService}
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
