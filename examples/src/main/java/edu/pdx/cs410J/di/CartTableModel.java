package edu.pdx.cs410J.di;

import javax.swing.table.AbstractTableModel;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * A swing table mode for the shopping cart
 */
public class CartTableModel extends AbstractTableModel
{
    private static final int TITLE_COLUMN = 0;
    private static final int PRICE_COLUMN = 1;

    private List<Book> books = new ArrayList<Book>();
    private NumberFormat PRICE_FORMAT = NumberFormat.getCurrencyInstance();

    public int getRowCount()
    {
        return books.size() + 1;
    }

    public int getColumnCount()
    {
        return 2;
    }

    public Object getValueAt( int row, int column )
    {
        if (isLastRow(row)) {
            switch (column) {
                case TITLE_COLUMN:
                    return "Total";
                case PRICE_COLUMN:
                    double total = 0.0;
                    for (Book book : books) {
                        total += book.getPrice();
                    }
                    return PRICE_FORMAT.format( total );
                default:
                    throw new IllegalArgumentException( "Unknown column " + column );
            }
            
        } else {
            Book book = this.books.get(row);
            switch (column) {
                case TITLE_COLUMN:
                    return book.getTitle();
                case PRICE_COLUMN:
                    return NumberFormat.getCurrencyInstance().format( book.getPrice() );
                default:
                    throw new IllegalArgumentException( "Unknown column " + column );
            }
        }
    }

    private boolean isLastRow( int row )
    {
        return row == getRowCount() - 1;
    }

    public void addBook( Book book )
    {
        int row = this.books.size();
        this.books.add(book);
        fireTableRowsInserted( row, row );
    }

    @Override
    public String getColumnName( int index )
    {
        switch (index) {
            case TITLE_COLUMN:
                return "Title";
            case PRICE_COLUMN:
                return "Price";
            default:
                throw new IllegalArgumentException( "Unknown column: " + index );
        }
    }

    /**
     * Returns the total amount of the items in the cart
     */
    public double getTotal() {
        String total = (String) getValueAt( getRowCount() - 1, PRICE_COLUMN );
        try
        {
            return PRICE_FORMAT.parse( total ).doubleValue();
        }
        catch ( ParseException e )
        {
            throw new IllegalStateException( "Unparsable total: " + total );
        }
    }
}
