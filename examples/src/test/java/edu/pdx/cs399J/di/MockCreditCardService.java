package edu.pdx.cs399J.di;

/**
 * A mock implementation of {@link edu.pdx.cs399J.di.CreditCardService}
 */
public class MockCreditCardService extends MockObject implements CreditCardService
{
    public CreditTransactionCode debit( CreditCard card, double amount )
    {
        shouldNotInvoke();
        return null;  // dead code
    }
}
