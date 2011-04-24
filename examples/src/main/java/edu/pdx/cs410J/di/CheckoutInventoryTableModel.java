package edu.pdx.cs410J.di;

/**
 * A subclass of {@link BookInventoryTableModel} that adds an extra column.
 */
class CheckoutInventoryTableModel extends BookInventoryTableModel
{
    public CheckoutInventoryTableModel( BookInventory inventory )
    {
        super( inventory );
    }

    @Override
    public int getColumnCount()
    {
        return super.getColumnCount() + 1;
    }

    @Override
    public String getColumnName( int column )
    {
        if ( isLastColumn( column ) )
        {
            return "";
        }
        else
        {
            return super.getColumnName( column );
        }
    }

    @Override
    public Object getValueAt( int row, int column )
    {
        if ( isLastColumn( column ) )
        {
            return getBook( row );
        }
        else
        {
            return super.getValueAt( row, column );
        }
    }

    public boolean isLastColumn( int column )
    {
        return column == this.getColumnCount() -1;
    }

    @Override
    public boolean isCellEditable( int row, int column )
    {
        return isLastColumn( column ) || super.isCellEditable( row, column );
    }
}
