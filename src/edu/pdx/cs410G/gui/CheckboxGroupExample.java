package edu.pdx.cs410J.gui;

import java.awt.*;

/**
 * This class demonstrates an AWT checkbox group
 */
public class CheckboxGroupExample extends Panel {

  /**
   * Create some {@link java.awt.Checkbox checkboxes} in a {@link
   * java.awt.CheckboxGroup group and add them to this panel
   */
  public CheckboxGroupExample() {
    CheckboxGroup group = new CheckboxGroup();
    Checkbox cb1 = new Checkbox("UNIX", true, group);
    Checkbox cb2 = new Checkbox("Windows", false, group);
    Checkbox cb3 = new Checkbox("Macintosh");
    cb3.setState(false);
    cb3.setCheckboxGroup(group);
    this.add(cb1);
    this.add(cb2);
    this.add(cb3);
  }

  /**
   * Create a new Frame and add a CheckboxGroupExample to it
   */
  public static void main(String[] args) {
    Frame frame = new Frame("CheckboxGroup example");
    frame.add(new CheckboxGroupExample());
    frame.pack();
    frame.setVisible(true);
  }

}
