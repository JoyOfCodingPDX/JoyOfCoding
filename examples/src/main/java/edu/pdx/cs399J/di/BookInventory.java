package edu.pdx.cs399J.di;

/**
 * The functionality required for an inventory of books
 */
public interface BookInventory
{
    /**
     * Removes a book from the inventory
     * @param book The book to remove
     */
    void remove( Book book );
}
