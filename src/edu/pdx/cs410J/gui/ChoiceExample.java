package edu.pdx.cs410J.gui;

import java.awt.*;

/**
 * This class demonstrates an AWT choice widget
 */
public class ChoiceExample extends Panel {

  /**
   * Create a {@link java.awt.Choice choice} add it to this panel
   */
  public ChoiceExample() {
    Choice choice = new Choice();
    choice.add("Monday");
    choice.add("Tuesday");
    choice.add("Wednesday");
    choice.add("Thursday");
    choice.add("Friday");
    choice.add("Saturday");
    choice.insert("Sunday", 0);
    this.add(choice);
  }

  /**
   * Create a new {@link java.awt.Frame Frame} and add a ChoiceExample
   * to it 
   */
  public static void main(String[] args) {
    Frame frame = new Frame("Choice example");
    frame.add(new ChoiceExample());
    frame.pack();
    frame.setVisible(true);
  }

}
