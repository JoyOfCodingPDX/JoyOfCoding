package edu.pdx.cs410J.di;

import com.google.inject.Inject;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

public class CheckoutPanel extends JPanel
{
    private final CartTableModel cartTableModel;

    private final Logger logger;

    @Inject
    public CheckoutPanel( BookInventory inventory, CreditCardService cardService, Logger logger ) {
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        this.logger = logger;

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
        this.add( new PaymentPanel( cardService ) );

        this.add( Box.createVerticalGlue() );

        this.setPreferredSize( new Dimension( 600, 400) );
    }

    private void addToCart(Book book) {
        logger.fine("Adding " + book + " to cart");
        this.cartTableModel.addBook(book);
    }

    private class PaymentPanel extends JPanel
    {
        public PaymentPanel( final CreditCardService cardService ) {
            setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );

            add(new JLabel( "Credit Card" ));
            add( Box.createHorizontalStrut( 5 ));
            final JTextField creditCard = new JTextField(30);
            add(creditCard);
            add( Box.createHorizontalStrut( 5 ));
            JButton purchase = new JButton("Purchase");
            purchase.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent actionEvent )
                {
                    double total = cartTableModel.getTotal();
                    String cardNumber = creditCard.getText();
                    String confirmation = "Do you want to charge " + total + " to your " + cardNumber + " card?";
                    String title = "Confirm charge";
                    int confirm =
                        JOptionPane.showConfirmDialog( PaymentPanel.this, confirmation, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
                    if (confirm == JOptionPane.YES_OPTION) {
                        CreditCard card = new CreditCard( cardNumber );
                        cardService.debit( card, total );
                    }
                }
            });
            add(Box.createHorizontalStrut( 5 ));
            add(purchase);
        }
    }
}
