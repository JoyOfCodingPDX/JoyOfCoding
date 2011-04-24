package edu.pdx.cs410J.di;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link BookDatabase} cass
 */
public class BookDatabaseTest
{
    private static int nextInt = 0;

    /**
     * Put temp files in the current working directory
     */
    private final File directory = new File( System.getProperty( "user.dir" ) );

    private String fileName;


    @Before
    public void setUp() {

      this.fileName = generateFileName();
    }

    @After
    public void tearDown() {
      File dataFile = new File(directory, fileName);
      dataFile.delete();
    }

    /**
     * Tests that a book can be added to a book database and is persisted
     */
    @Test
    public void testAddBook() throws JAXBException, IOException
    {
        BookDatabase db = new BookDatabase( this.directory, fileName );
        Book book = new Book( "TestTitle", "TestAuthor", 1.23 );
        db.add( book );
        assertEquals( 1, db.getCopies( book ) );

        db = new BookDatabase( this.directory, fileName );
        assertEquals( 1, db.getCopies( book ) );
    }

    /**
     * Tests that a multiple copies of the same book can be added to a book database and is persisted
     */
    @Test
    public void testAddSameBook() throws JAXBException, IOException
    {
        BookDatabase db = new BookDatabase( this.directory, fileName );
        Book book = new Book( "TestTitle", "TestAuthor", 1.23 );
        db.add( book );
        db.add( book );
        db.add( book );
        assertEquals( 3, db.getCopies( book ) );

        db = new BookDatabase( this.directory, fileName );
        assertEquals( 3, db.getCopies( book ) );
    }

    /**
     * Tests that a multiple copies of multiple books can be added to a book database and is persisted
     */
    @Test
    public void testAddMultipleBooks() throws JAXBException, IOException
    {
        BookDatabase db = new BookDatabase( this.directory, fileName );
        Book book1 = new Book( "TestTitle1", "TestAuthor1", 1.23 );
        db.add( book1, book1, book1 );
        assertEquals( 3, db.getCopies( book1 ) );

        Book book2 = new Book( "TestTitle2", "TestAuthor2", 1.23 );
        db.add( book2, book2 );
        assertEquals( 2, db.getCopies( book2 ) );

        db = new BookDatabase( this.directory, fileName );
        assertEquals( 3, db.getCopies( book1 ) );
        assertEquals( 2, db.getCopies( book2 ) );
    }

    private String generateFileName()
    {
        return this.getClass().getName() + "_" + (nextInt++) + "_" + System.currentTimeMillis();
    }
}
