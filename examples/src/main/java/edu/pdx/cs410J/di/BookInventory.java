package edu.pdx.cs410J.di;

import java.util.Set;

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

    /**
     * Adds some books to the inventory
     * @param books
     *        A bunch of bookx
     */
    void add(Book... books);

    /**
     * Returns the number of copies of the given book in the inventory
     * @param book
     *        The book of interest
     * @return the number of copies of <code>book</code>
     */
    int getCopies( Book book );

    /**
     * Returns all of the books in this inventory
     * @return all of the books in this inventory
     */
    Set<Book> getBooks();
}
