package edu.pdx.cs399J.di;

import java.io.File;
import java.util.Map;

/**
 * An implementation of {@link BookInventory} that stores a database of books in a flat file
 */
public class BookDatabase implements BookInventory
{
    private final File directory;

    private final String fileName;

    public BookDatabase( File directory, String fileName )
    {
        this.directory = directory;
        this.fileName = fileName;
    }

    public void remove( Book book )
    {
        throw new UnsupportedOperationException( "This method is not implemented yet" );
    }
}
