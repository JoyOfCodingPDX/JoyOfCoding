package edu.pdx.cs410J.di;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An implementation of {@link BookInventory} that stores a database of books in a flat file
 */
@Singleton
public class BookDatabase extends JaxbDatabase implements BookInventory
{

    private final Map<Book, AtomicInteger> inventory;

    /**
     * Creates a new database of books stored in a file named "books.xml"
     *
     * @param directory
     *        The directory in which the data file will reside
     * @throws IOException
     *         If the data file cannot be created
     * @throws JAXBException
     *         If we cannot create the XML context
     */
    @Inject
    public BookDatabase( @DataDirectory File directory ) throws JAXBException, IOException
    {
      this( directory, "books.xml" );
    }

    /**
     * Creates a new database of books.  This method is package-protected for testing purposes.
     *
     * @param directory
     *        The directory in which the data file will reside
     * @param fileName
     *        The name of the file to store the book inventory
     *
     * @throws IOException
     *         If the data file cannot be created
     * @throws JAXBException
     *         If we cannot create the XML context
     */
    BookDatabase( File directory, String fileName ) throws IOException, JAXBException
    {
        super( directory, fileName, BookDatabase.XmlBookDatabase.class,
               BookDatabase.XmlBookDatabase.BookCount.class, Book.class );

        XmlBookDatabase xml = (XmlBookDatabase) readFile();
        if (xml != null) {
            this.inventory = xml.getMap();
        } else {
          this.inventory = new HashMap<Book, AtomicInteger>();
        }
    }

    public synchronized void remove( Book book )
    {
        AtomicInteger count = inventory.get(book);
        if (count == null || count.get() == 0) {
          throw new IllegalStateException("We're out of " + book);

        } else {
          count.decrementAndGet();
          writeInventory();
        }
    }

    public synchronized void add(Book... books) {
      for (Book book : books) {
          AtomicInteger count = inventory.get(book);
          if (count == null) {
              count = new AtomicInteger( 0 );
              inventory.put(book, count);
          }
          count.incrementAndGet();
      }

      writeInventory();
    }

    private synchronized void writeInventory()
    {
        writeXml( new XmlBookDatabase( this.inventory) );
    }

    public int getCopies( Book book )
    {
        AtomicInteger count = inventory.get(book);
        if (count == null) {
            return 0;

        } else {
            return count.get();
        }
    }

    public Set<Book> getBooks()
    {
        return inventory.keySet();
    }

    /**
     * JAXB can't marshall a <code>HashMap</code>, so we need to use this stupid class to represent a book database.
     */
    @XmlRootElement(name="book-database")
    private static class XmlBookDatabase
    {
        @XmlElementWrapper(name="books")
        private List<BookCount> counts;


        /**
         * For unmarshalling
         */
        public XmlBookDatabase() {

        }

        public XmlBookDatabase( Map<Book, AtomicInteger> inventory )
        {
            counts = new ArrayList<BookCount>(inventory.size());
            for (Map.Entry<Book, AtomicInteger> count : inventory.entrySet()) {
                counts.add(new BookCount(count.getKey(), count.getValue()));
            }
        }

        public Map<Book, AtomicInteger> getMap()
        {
            Map<Book, AtomicInteger> map = new HashMap<Book, AtomicInteger>(counts.size());
            for (BookCount count : counts) {
                Book book = count.getBook();
                AtomicInteger ai = map.get(book);
                if (ai == null) {
                    ai = new AtomicInteger( 0 );
                    map.put(book, ai);
                }

                ai.addAndGet( count.getCount() );
            }
            return map;
        }

        @XmlRootElement(name="count")
        private static class BookCount
        {
            @XmlElement
            private Book book;

            @XmlAttribute
            private int count;

            /**
             * For unmarshalling
             */
            public BookCount() {

            }

            public BookCount( Book book, AtomicInteger count )
            {
                this.book = book;
                this.count = count.get();
            }

            public Book getBook()
            {
                return book;
            }

            public int getCount()
            {
                return count;
            }
        }
    }
}
