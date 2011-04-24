package edu.pdx.cs410J.di;

import com.google.inject.Inject;

import javax.swing.*;

/**
 * A simple Java swing user interface for a Book Store application
 */
public class BookStoreGUI extends JFrame
{
    @Inject
    public BookStoreGUI(CheckoutPanel checkout) {
        super("Bookstore Terminal");

        JTabbedPane tabs = new JTabbedPane( );
        tabs.add( "Checkout", checkout );

        this.add(tabs);
    }
}
