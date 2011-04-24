package edu.pdx.cs410J.gwt.client;

import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.Button;

/**
 */
public class StackPanelExample extends Example {
  public StackPanelExample() {
    super("Stack Panel");

    StackPanel stack = new StackPanel();
    stack.add(new Button("One"), "One");
    stack.add(new Button("Two"), "Two");
    stack.add(new Button("Three"), "Three");
    stack.add(new Button("Four"), "Four");
    add(stack);
  }
}
