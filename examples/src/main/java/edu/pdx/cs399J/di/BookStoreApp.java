package edu.pdx.cs399J.di;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

/**
 * A main program that launches a book store using the "production" {@link edu.pdx.cs399J.di.BookInventory} and
 * {@link edu.pdx.cs399J.di.CreditCardService}
 */
public class BookStoreApp
{
   public static void main(String... args) throws JAXBException, IOException
   {
       String tmpdir = System.getProperty( "java.io.tmpdir" );
       File directory = new File( tmpdir );
       BookInventory inventory = new BookDatabase( directory, "books.txt" );
       addBooks(inventory);
       CreditCardService cardService = new FirstBankOfPSU( "localhost", 8080 );
       BookStore store = new BookStore(inventory, cardService);

       BookStoreGUI gui = new BookStoreGUI(new CheckoutPanel( inventory ));
       gui.pack();
       gui.setVisible( true );
   }

    private static void addBooks( BookInventory inventory )
    {
        addBook( inventory, new Book( "The Pragmatic Programmer", "Andrew Hunt", 29.95 ), 4 );
        addBook( inventory, new Book("Winning", "Jack Welch", 15.95), 3 );
        addBook( inventory, new Book( "Agile Estimating and Planning", "Mike Cohn", 35.99), 2);
    }

    private static void addBook( BookInventory inventory, Book book, int copies )
    {
        inventory.add( Collections.nCopies( copies, book ).toArray(new Book[copies]) );
    }
}
