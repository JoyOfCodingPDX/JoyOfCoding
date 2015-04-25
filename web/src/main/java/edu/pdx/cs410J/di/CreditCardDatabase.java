package edu.pdx.cs410J.di;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Stores {@link CreditCard}s in an XML file
 */
public class CreditCardDatabase extends JaxbDatabase
{
    private final Map<CreditCard, Double> balances;

    @Inject
    public CreditCardDatabase(@DataDirectory File directory) throws JAXBException, IOException
    {
       this( directory, "CreditCards.xml"); 
    }

    CreditCardDatabase( File directory, String fileName )
        throws IOException, JAXBException
    {
        super( directory, fileName, XmlCreditCardDatabase.class, XmlCreditCardDatabase.CreditCardBalance.class, CreditCard.class );

        XmlCreditCardDatabase xml = (XmlCreditCardDatabase) readFile();

        if (xml != null) {
            this.balances = xml.getMap();
        } else {
          this.balances = Maps.newHashMap();
        }
    }

    synchronized void setBalance(CreditCard card, double balance) {
        this.balances.put(card, balance);

        writeDatabase();
    }

    public synchronized void debit(CreditCard card, double amount) {
        double balance = balances.get(card);
        balance -= amount;
        this.balances.put(card, balance);

        writeDatabase();
    }

    private void writeDatabase() {
      writeXml( new XmlCreditCardDatabase( this.balances) ); 
    }

    public ImmutableMap<CreditCard, Double> getBalances()
    {
        return ImmutableMap.copyOf( this.balances );
    }

    /**
     * JAXB can't marshall a <code>HashMap</code>, so we need to use this stupid class to represent a CreditCard database.
     */
    @XmlRootElement(name="CreditCard-database")
    private static class XmlCreditCardDatabase
    {
        @XmlElementWrapper(name="CreditCards")
        private List<CreditCardBalance> balances;


        /**
         * For unmarshalling
         */
        public XmlCreditCardDatabase() {

        }

        public XmlCreditCardDatabase( Map<CreditCard, Double> inventory )
        {
            balances = new ArrayList<CreditCardBalance>(inventory.size());
            for (Map.Entry<CreditCard, Double> count : inventory.entrySet()) {
                balances.add(new CreditCardBalance(count.getKey(), count.getValue()));
            }
        }

        public Map<CreditCard, Double> getMap()
        {
            Map<CreditCard, Double> map = Maps.newHashMap();
            for ( CreditCardBalance balance : balances ) {
                map.put(balance.getCard(), balance.getBalance());
            }
            return map;
        }

        @XmlRootElement(name="balance")
        private static class CreditCardBalance
        {
            @XmlElement
            private CreditCard card;

            @XmlAttribute
            private double balance;

            /**
             * For unmarshalling
             */
            public CreditCardBalance() {

            }

            public CreditCardBalance( CreditCard card, Double balance )
            {
                this.card = card;
                this.balance = balance;
            }

            public CreditCard getCard()
            {
                return card;
            }

            public double getBalance()
            {
                return balance;
            }
        }
    }

}
