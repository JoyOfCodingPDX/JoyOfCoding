package edu.pdx.cs410J.gwt.client;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.PasswordTextBox;

/**
 * Demonstrates GWT text boxes
 */
public class TextBoxExample extends Example {
  public TextBoxExample() {
    super("Text Boxes");

    TextBox text = new TextBox();
    text.setVisibleLength(15);
    text.setMaxLength(10);
    add(text);

    TextBox readOnly = new TextBox();
    readOnly.setReadOnly(true);
    readOnly.setVisibleLength(15);
    readOnly.setText("Read only text");
    add(readOnly);

    PasswordTextBox password = new PasswordTextBox();
    password.setVisibleLength(15);
    add(password);
  }
}
