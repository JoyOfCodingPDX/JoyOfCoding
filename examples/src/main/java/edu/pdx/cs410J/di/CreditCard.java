package edu.pdx.cs410J.di;

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

  @Override
  public String toString() {
    return "Credit card for " + this.number;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CreditCard that = (CreditCard) o;

    return !(number != null ? !number.equals(that.number) : that.number != null);

  }

  @Override
  public int hashCode() {
    return number != null ? number.hashCode() : 0;
  }
}
