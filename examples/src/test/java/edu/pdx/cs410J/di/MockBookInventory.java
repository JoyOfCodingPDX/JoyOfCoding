package edu.pdx.cs410J.di;

import java.util.Set;

/**
 * A mock implementation of {@link BookInventory} used for testing
 */
public class MockBookInventory extends MockObject implements BookInventory
{
    public void remove( Book book )
    {
        shouldNotInvoke();
    }

    public void add( Book... books )
    {
        shouldNotInvoke();
    }

    public int getCopies( Book book )
    {
        shouldNotInvoke();
        return 0;
    }

    public Set<Book> getBooks()
    {
        shouldNotInvoke();
        return null;
    }

}
