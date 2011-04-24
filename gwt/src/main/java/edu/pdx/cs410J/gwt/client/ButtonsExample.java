package edu.pdx.cs410J.gwt.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * Demonstrates the various kinds of GWT buttons
 */
public class ButtonsExample extends Example {
  public ButtonsExample() {
    super("Buttons");

    Button button = new Button("Disabled button");
    button.setEnabled(false);
    add(button);

    CheckBox check = new CheckBox("Check box");
    check.setChecked(true);
    add(check);

    PushButton push = new PushButton("Click me", "Clicked");
    add(push);

    ToggleButton toggle = new ToggleButton("Up", "Down");
    add(toggle);

  }
}
