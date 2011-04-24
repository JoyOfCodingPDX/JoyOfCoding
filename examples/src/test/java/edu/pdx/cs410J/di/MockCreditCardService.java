package edu.pdx.cs410J.di;

/**
 * A mock implementation of {@link edu.pdx.cs410J.di.CreditCardService}
 */
public class MockCreditCardService extends MockObject implements CreditCardService
{
    public CreditTransactionCode debit( CreditCard card, double amount )
    {
        shouldNotInvoke();
        return null;  // dead code
    }
}
