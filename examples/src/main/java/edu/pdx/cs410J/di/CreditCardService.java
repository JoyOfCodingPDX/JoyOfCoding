package edu.pdx.cs410J.di;

/**
 * The functionality required for a credit card service
 */
public interface CreditCardService
{
    /**
     * Charges the given amount to the given credit card
     * @param card The card to charge
     * @param amount The amount to charge
     * @return The code that conveys the result of the debit transaction
     */
    CreditTransactionCode debit( CreditCard card, double amount );
}
