package edu.pdx.cs410J.di;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

/**
 * A table that shows the inventory of books and lets the user select one to be added to a shopping cart.  When a book
 * is selected, a {@link TableModelEvent} is fired to listeners on this table.
 */
class CheckoutInventoryTable extends JTable
{

    public CheckoutInventoryTable( CheckoutInventoryTableModel model )
    {
        super(model);
        TableColumn column = this.getColumnModel().getColumn( model.getColumnCount() - 1 );
        column.setCellEditor( new TableButton( model ) );
        column.setCellRenderer( new TableButton( model ) );
    }

    /**
     * Renders a button that
     */
    private static class TableButton implements TableCellRenderer, TableCellEditor
    {
        private final BookInventoryTableModel model;

        public TableButton( BookInventoryTableModel model )
        {
            this.model = model;
        }

        public Component getTableCellRendererComponent( JTable table, Object value,  boolean isSelected, boolean hasFocus, int row, int column )
        {
            return getButton( row );
        }

        private Component getButton( final int row )
        {
            JButton button = new JButton( "Add" );
            button.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent event )
                {
                   model.fireTableChanged( new TableModelEvent( model, row, row, model.getColumnCount() -1 ) );
                }
            });
            return button;
        }

        public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected, int row, int column )
        {
            return getButton(row);
        }

        public Object getCellEditorValue()
        {
            return null;
        }

        public boolean isCellEditable( EventObject eventObject )
        {
            return true;
        }

        public boolean shouldSelectCell( EventObject eventObject )
        {
            return true;
        }

        public boolean stopCellEditing()
        {
            return true;
        }

        public void cancelCellEditing()
        {

        }

        public void addCellEditorListener( CellEditorListener cellEditorListener )
        {

        }

        public void removeCellEditorListener( CellEditorListener cellEditorListener )
        {

        }
    }
}
