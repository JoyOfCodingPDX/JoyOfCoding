package edu.pdx.cs.joy.di;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import jakarta.ws.rs.*;

import java.util.Map;

/**
 * A Credit Card service that exposes its behavior via REST
 */
@Path("/rest/creditCard")
public class RestfulCreditCardService
{
    private final CreditCardDatabase cards;

    @Inject
    public RestfulCreditCardService(CreditCardDatabase cards) {
        this.cards = cards;
    }

    @GET
    @Produces("text/plain")
    public String allCardBalances() {
        StringBuilder sb = new StringBuilder();

        ImmutableMap<CreditCard, Double> balances = cards.getBalances();
        for ( Map.Entry<CreditCard, Double> entry : balances.entrySet() ) {
            sb.append(entry.getKey().getNumber());
            sb.append("\t");
            sb.append(entry.getValue());
            sb.append("\n");
        }

        return sb.toString();
    }

}
