package edu.pdx.cs410J.gwt.client;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.*;

/**
 * Demonstrates GWT's {@link DeckPanel}
 */
public class DeckPanelExample extends Example {
  public DeckPanelExample() {
    super("Deck Panel");

    DockPanel dock = new DockPanel();
    final DeckPanel deck = new DeckPanel();
    final ListBox list = new ListBox();
    list.addChangeHandler(new ChangeHandler() {
        public void onChange( ChangeEvent changeEvent )
        {
            int index = list.getSelectedIndex();
            deck.showWidget(index);
        }
    });

    for (String label : new String[] { "One", "Two", "Three", "Four"}) {
      list.addItem(label);
      deck.add(new Button(label));
    }
    deck.showWidget(0);

    dock.add(list, DockPanel.NORTH);
    dock.add(deck, DockPanel.CENTER);
    add(dock);
  }
}
