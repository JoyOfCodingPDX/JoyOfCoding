package edu.pdx.cs410J.di;

import java.util.List;

/**
 * A book store that depends on a {@link BookInventory} and a {@link CreditCardService}
 */
public class BookStore
{
    private final BookInventory inventory;

    private final CreditCardService cardService;

    public BookStore( BookInventory inventory, CreditCardService cardService )
    {
        this.inventory = inventory;
        this.cardService = cardService;
    }

    /**
     * Purchases a number of books from this book store
     *
     * @return the total amount of the purchase
     * @throws CreditCardTransactionException
     *         If a problem occurs while paying for the purchase
     */
    public double purchase( List<Book> books, CreditCard card) {
        double total = 0.0d;
        for (Book book : books) {
            inventory.remove(book);
            total += book.getPrice();
        }

        CreditTransactionCode code = cardService.debit(card, total);
        if (code == CreditTransactionCode.SUCCESS ) {
            return total;

        } else {
            throw new CreditCardTransactionException(code);
        }
    }

}
