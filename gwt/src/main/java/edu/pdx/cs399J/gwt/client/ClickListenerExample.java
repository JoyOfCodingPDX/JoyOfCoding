package edu.pdx.cs399J.gwt.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Window;

/**
 */
public class ClickListenerExample extends Example {
  public ClickListenerExample() {
    super("Click Listener");

    Button button = new Button("Click me");
    button.addClickListener(new ClickListener() {
      public void onClick(Widget widget) {
        Window.alert("I was clicked");
      }
    });
    add(button);
  }
}
