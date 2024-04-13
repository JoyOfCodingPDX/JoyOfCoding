package edu.pdx.cs.joy.di;

/**
 * A mock implementation of {@link CreditCardService}
 */
public class MockCreditCardService extends MockObject implements CreditCardService
{
    public CreditTransactionCode debit( CreditCard card, double amount )
    {
        shouldNotInvoke();
        return null;  // dead code
    }
}
