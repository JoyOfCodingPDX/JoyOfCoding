package edu.pdx.cs399J.gui;

import java.awt.*;

/**
 * This class demonstrates an AWT checkbox
 */
public class CheckboxExample extends Panel {

  /**
   * Create some {@link java.awt.Checkbox checkboxes} and add them to
   * this panel
   */
  public CheckboxExample() {
    Checkbox cb1 = new Checkbox("No");
    Checkbox cb2 = new Checkbox("Yes", true);
    Checkbox cb3 = new Checkbox("Maybe");
    cb3.setState(true);
    this.add(cb1);
    this.add(cb2);
    this.add(cb3);
  }

  /**
   * Create a new Frame and add a CheckboxExample to it
   */
  public static void main(String[] args) {
    Frame frame = new Frame("Checkbox example");
    frame.add(new CheckboxExample());
    frame.pack();
    frame.setVisible(true);
  }

}
