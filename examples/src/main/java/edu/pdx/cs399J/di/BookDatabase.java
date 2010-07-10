package edu.pdx.cs399J.di;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An implementation of {@link BookInventory} that stores a database of books in a flat file
 */
public class BookDatabase implements BookInventory
{
    private final File directory;

    private final String fileName;

    private final Map<Book, AtomicInteger> inventory;

    private final JAXBContext xmlContext;

    /**
     * Creates a new database of books
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
    public BookDatabase( File directory, String fileName ) throws IOException, JAXBException
    {
        this.directory = directory;
        this.fileName = fileName;

        this.directory.mkdirs();
        if (!this.directory.exists()) {
          throw new IOException( "Could not create data directory: " + this.directory);
        }

        this.xmlContext = JAXBContext.newInstance( XmlBookDatabase.class, XmlBookDatabase.BookCount.class, Book.class );

        File file = new File(this.directory, this.fileName);

        System.out.println("Opening book database in " + file);

        if (file.exists()) {
          Unmarshaller unmarshaller = this.xmlContext.createUnmarshaller();
          XmlBookDatabase xml = (XmlBookDatabase) unmarshaller.unmarshal( file );
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
        XmlBookDatabase xml = new XmlBookDatabase(this.inventory);
        try
        {
            Marshaller marshaller = this.xmlContext.createMarshaller();
            marshaller.marshal( xml, new File(this.directory, this.fileName) );
        }
        catch ( JAXBException ex )
        {
            throw new IllegalStateException( "Could not save inventory", ex);
        }
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
