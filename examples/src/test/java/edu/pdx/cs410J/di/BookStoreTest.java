package edu.pdx.cs410J.di;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Demonstrates how dependency injection makes it easier to test a {@link BookStore}
 */
public class BookStoreTest
{
    /**
     * Tests that a purchased book is removed from the {@link BookInventory} when it is purchased
     */
    @Test
    public void testBookIsPurchased() {
        final Book testBook = new Book("title", "author", 1.00d);
        final Book[] removedBook = new Book[1];
        BookInventory inventory = new MockBookInventory() {
            @Override
            public void remove( Book book )
            {
                removedBook[0] = book;
            }
        };
        CreditCardService cardService = new MockCreditCardService() {
            @Override
            public CreditTransactionCode debit( CreditCard card, double amount )
            {
                return CreditTransactionCode.SUCCESS;
            }
        };
        BookStore store = new BookStore(inventory, cardService);
        CreditCard card = new CreditCard( "123" );
        double total = store.purchase( Collections.singletonList(testBook), card );
        assertEquals( testBook.getPrice(), total, 0.0d );

        final Book removed = removedBook[0];
        assertNotNull( removed );
        assertEquals(testBook.getTitle(), removed.getTitle());
        assertEquals(testBook.getAuthor(), removed.getAuthor());
        assertEquals(testBook.getPrice(), removed.getPrice(), 0.0d);
    }

    /**
     * Tests that the credit card is indeed passed to the {@link CreditCardService}
     */
    @Test
    public void testCreditCardIsCharged() {
        Book book = mock(Book.class);
        double price = 1.00d;
        when( book.getPrice() ).thenReturn( price );

        BookInventory inventory = mock(BookInventory.class);

        CreditCardService cardService = mock(CreditCardService.class);
        when(cardService.debit(any( CreditCard.class ), anyDouble() )).thenReturn( CreditTransactionCode.SUCCESS );

        BookStore store = new BookStore(inventory, cardService);

        CreditCard card = mock(CreditCard.class);

        double total = store.purchase( Collections.singletonList(book), card );
        assertEquals( book.getPrice(), total, 0.0d );

        verify( cardService ).debit( card, price );      
    }
}
