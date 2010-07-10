package edu.pdx.cs399J.di;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class CheckoutPanel extends JPanel
{
    public CheckoutPanel( final CheckoutInventoryTable inventoryTable ) {
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        inventoryTable.getModel().addTableModelListener( new TableModelListener() {

            public void tableChanged( TableModelEvent event )
            {
                assert event.getFirstRow() == event.getLastRow();
                Book book = (Book) inventoryTable.getValueAt( event.getFirstRow(), event.getColumn() );
                addToCart( book );
            }
        });
        this.add( new JScrollPane( inventoryTable ));

        this.add( Box.createVerticalGlue() );
    }

    private void addToCart(Book book) {
        System.out.println("Adding " + book + " to cart");
    }

}
