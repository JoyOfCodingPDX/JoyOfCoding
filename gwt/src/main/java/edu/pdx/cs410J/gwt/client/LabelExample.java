package edu.pdx.cs410J.gwt.client;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HTML;

/**
 * Demonstrates a GWT {@link com.google.gwt.user.client.ui.Label}
 */
public class LabelExample extends Example {

  public LabelExample() {
    super("Labels");

    add(new Label("This is a Label"));
    add(new HTML("This label has <i>HTML</i>"));
    String longString = "This label is very long and has word wrap enabled";
    add(new Label(longString, true));
    String longString2 = "This label is very long and does not have word wrap enabled";
    add(new Label(longString2, false));
  }
}
