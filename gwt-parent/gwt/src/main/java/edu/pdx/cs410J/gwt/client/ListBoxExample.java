package edu.pdx.cs410J.gwt.client;

import com.google.gwt.user.client.ui.ListBox;

/**
 * Demonstrates the GWT {@link ListBox}
 */
public class ListBoxExample extends Example {
  public ListBoxExample() {
    super("List Box");

    ListBox box = new ListBox();
    box.addItem("Monday");
    box.addItem("Tuesday");
    box.addItem("Wednesday");
    box.addItem("Thursday");
    box.addItem("Friday");

    add(box);

    ListBox multi = new ListBox(true);
    multi.addItem("Chocolate");
    multi.addItem("Vanilla");
    multi.addItem("Strawberry");

    add(multi);
  }
}
