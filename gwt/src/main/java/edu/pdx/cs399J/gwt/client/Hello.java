package edu.pdx.cs399J.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A basic GWT class
 */
public class Hello implements EntryPoint {
  public void onModuleLoad() {
    Button button = new Button("Hello World");
    button.addClickListener(new ClickListener() {
		public void onClick(Widget arg0) {
			Window.alert("Hello from GWT");
		}
    	
    });
    RootPanel.get().add(button);
  }
}
