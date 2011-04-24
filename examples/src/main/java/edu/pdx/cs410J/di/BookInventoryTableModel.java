package edu.pdx.cs410J.di;

import javax.swing.table.AbstractTableModel;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Set;

/**
 * A swing table model that lists the contents of a {@link BookInventory}
 */
public class BookInventoryTableModel extends AbstractTableModel
{
    private static final int AUTHOR_COLUMN = 0;
    private static final int TITLE_COLUMN = 1;
    private static final int PRICE_COLUMN = 2;
    private static final int QUANTITY_COLUMN = 3;


    protected final BookInventory inventory;

    public BookInventoryTableModel( BookInventory inventory )
    {
        this.inventory = inventory;
    }

    public int getRowCount()
    {
        return inventory.getBooks().size();
    }

    public int getColumnCount()
    {
        return 4;
    }

    @Override
    public String getColumnName( int column )
    {
        switch(column) {
            case AUTHOR_COLUMN:
                return "Author";
            case TITLE_COLUMN:
                return "Title";
            case PRICE_COLUMN:
                return "Price";
            case QUANTITY_COLUMN:
                return "Quantity";
            default:
                throw new IllegalArgumentException( "Unknown column: " + column );
        }
    }

    public Object getValueAt( int row, int column )
    {
        Book book = getBook( row );

        switch(column) {
            case AUTHOR_COLUMN:
                return book.getAuthor();
            case TITLE_COLUMN:
                return book.getTitle();
            case PRICE_COLUMN:
                return NumberFormat.getCurrencyInstance().format( book.getPrice() );
            case QUANTITY_COLUMN:
                return inventory.getCopies( book );
            default:
                throw new IllegalArgumentException( "Unknown column: " + column );
        }
    }

    public Book getBook( int i )
    {
        Set<Book> books = inventory.getBooks();
        assert i < books.size();
        Iterator<Book> iter = books.iterator();
        for (int j = 0; j < i; j++) {
            iter.next();
        }
        return iter.next();
    }

    public Book decrementInventry( int row )
    {
        Book book = getBook( row );
        inventory.remove( book );
        fireTableCellUpdated( row, QUANTITY_COLUMN );
        return book;
    }
}
