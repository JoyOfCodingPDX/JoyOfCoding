package edu.pdx.cs410J.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;

/**
 * A basic GWT class
 */
public class Hello implements EntryPoint {
  public void onModuleLoad() {
    Button button = new Button("Hello World");
    button.addClickHandler(new ClickHandler() {
        public void onClick( ClickEvent clickEvent )
        {
            Window.alert("Hello from GWT");
        }
    });
      RootPanel rootPanel = RootPanel.get("hello");
      if (rootPanel != null) {
        rootPanel.add(button);
      }
  }
}
