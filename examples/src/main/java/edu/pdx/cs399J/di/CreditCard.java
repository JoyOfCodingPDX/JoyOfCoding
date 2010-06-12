package edu.pdx.cs399J.di;

/**
 * A class that represents a credit card
 */
public class CreditCard
{
    private final String number;

    /**
     * Creates a credit card with the given card number
     * @param number The card number
     */
    public CreditCard( String number )
    {
        this.number = number;
    }


    /**
     * Returns the number of this credit card
     */
    public String getNumber()
    {
        return number;
    }
}
