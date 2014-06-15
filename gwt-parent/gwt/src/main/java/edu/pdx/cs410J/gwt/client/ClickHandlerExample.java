package edu.pdx.cs410J.gwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;

/**
 * A simple class that pops up a dialog when a button is clicked
 */
public class ClickHandlerExample extends Example {
  public ClickHandlerExample() {
    super("Click Handler");

    Button button = new Button("Click me");
    button.addClickHandler(new ClickHandler() {
        public void onClick( ClickEvent clickEvent )
        {
            Window.alert("I was clicked");
        }
    });
    add(button);
  }
}
