package edu.pdx.cs399J.di;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CheckoutPanel extends JPanel
{
    private final CartTableModel cartTableModel;

    public CheckoutPanel( BookInventory inventory ) {
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        this.cartTableModel = new CartTableModel();

        final CheckoutInventoryTableModel inventoryModel = new CheckoutInventoryTableModel( inventory );
        inventoryModel.addTableModelListener( new TableModelListener() {

            public void tableChanged( TableModelEvent event )
            {
                if (inventoryModel.isLastColumn( event.getColumn() ) ) {
                    for (int i = event.getFirstRow(); i <= event.getLastRow(); i++) {
                        addToCart( inventoryModel.decrementInventry(event.getFirstRow()) );
                    }
                }
            }
        });
        this.add( new JScrollPane( new CheckoutInventoryTable( inventoryModel ) ));
        this.add( Box.createVerticalStrut( 20 ));
        this.add( new JScrollPane( new CartTable( cartTableModel ) ));
        this.add( Box.createVerticalStrut( 20 ));
        this.add( new PaymentPanel() );

        this.add( Box.createVerticalGlue() );

        this.setPreferredSize( new Dimension( 600, 400) );
    }

    private void addToCart(Book book) {
        System.out.println("Adding " + book + " to cart");
        this.cartTableModel.addBook(book);
    }

    private class PaymentPanel extends JPanel
    {
        public PaymentPanel() {
            setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );

            add(new JLabel( "Credit Card" ));
            add( Box.createHorizontalStrut( 5 ));
            JTextField creditCard = new JTextField(30);
            add(creditCard);
            add( Box.createHorizontalStrut( 5 ));
            JButton purchase = new JButton("Purchase");
            purchase.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent actionEvent )
                {
                    throw new UnsupportedOperationException( "This method is not implemented yet" );
                }
            });
            add(Box.createHorizontalStrut( 5 ));
            add(purchase);
        }
    }
}
