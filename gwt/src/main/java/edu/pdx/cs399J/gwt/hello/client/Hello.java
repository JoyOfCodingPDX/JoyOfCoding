package edu.pdx.cs399J.gwt.hello.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * A basic GWT class
 */
public class Hello implements EntryPoint {
  public void onModuleLoad() {
    Button button = new Button("Hello World");
    RootPanel.get().add(button);
  }
}
