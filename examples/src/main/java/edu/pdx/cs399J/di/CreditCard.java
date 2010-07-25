package edu.pdx.cs399J.di;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * A class that represents a credit card
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CreditCard
{
    private String number;

    /**
     * For marshalling
     */
    public CreditCard() {

    }

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
