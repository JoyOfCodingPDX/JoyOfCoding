package edu.pdx.cs410J.di;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A main program that launches a book store using the "production" {@link edu.pdx.cs410J.di.BookInventory} and
 * {@link edu.pdx.cs410J.di.CreditCardService}
 */
public class BookStoreApp
{
   public static void main(String... args) throws JAXBException, IOException
   {
       String tmpdir = System.getProperty( "java.io.tmpdir" );
       File directory = new File( tmpdir );
       BookInventory inventory = new BookDatabase( directory );
       addBooks(inventory);
       CreditCardService cardService = new FirstBankOfPSU( "localhost", 8080 );

       Logger logger = Logger.getLogger( "edu.pdx.cs410J.Logger" );
       logger.setLevel( Level.INFO );

       CheckoutPanel panel = new CheckoutPanel( inventory, cardService, logger );
       BookStoreGUI gui = new BookStoreGUI( panel );
       gui.pack();
       gui.setVisible( true );
   }

    protected static void addBooks( BookInventory inventory )
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
