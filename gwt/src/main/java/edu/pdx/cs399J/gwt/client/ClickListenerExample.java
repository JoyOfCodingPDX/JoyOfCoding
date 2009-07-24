package edu.pdx.cs399J.gwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;

/**
 */
public class ClickListenerExample extends Example {
  public ClickListenerExample() {
    super("Click Listener");

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
