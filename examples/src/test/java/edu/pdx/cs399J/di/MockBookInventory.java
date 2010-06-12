package edu.pdx.cs399J.di;

/**
 * A mock implementation of {@link BookInventory} used for testing
 */
public class MockBookInventory extends MockObject implements BookInventory
{
    public void remove( Book book )
    {
        shouldNotInvoke();
    }

}
