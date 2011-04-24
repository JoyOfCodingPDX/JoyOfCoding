package edu.pdx.cs410J.di;

import com.google.inject.Guice;
import com.google.inject.Injector;


public class GuicyBookStoreApp extends BookStoreApp
{
  public static void main(String... args) {
      Injector injector = Guice.createInjector( new BookStoreModule() );
      BookInventory inventory = injector.getInstance( BookInventory.class );
      addBooks( inventory );

      BookStoreGUI gui = injector.getInstance( BookStoreGUI.class );
      gui.pack();
      gui.setVisible( true );
  }

}
