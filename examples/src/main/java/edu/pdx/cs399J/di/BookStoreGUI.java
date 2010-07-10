package edu.pdx.cs399J.di;

import javax.swing.*;

/**
 * A simple Java swing user interface for a Book Store application
 */
public class BookStoreGUI extends JFrame
{
    public BookStoreGUI(CheckoutPanel checkout) {
        super("Bookstore Terminal");

        JTabbedPane tabs = new JTabbedPane( );
        tabs.add( "Checkout", checkout );

        this.add(tabs);
    }
}
