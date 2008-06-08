package edu.pdx.cs410G.awt;

import java.awt.*;

/**
 * This class demonstrates an AWT choice widget
 */
public class LabelExample extends Panel {

  /**
   * Create some {@link java.awt.Label labels} and adds them to this
   * panel 
   */
  public LabelExample() {
    Label l1 = new Label("A label");
    Label l2 = new Label("Left justified", Label.LEFT);
    Label l3 = new Label("Right", Label.RIGHT);
    Label l4 = new Label("Centered");
    l4.setAlignment(Label.CENTER);
    this.add(l1);
    this.add(l2);
    this.add(l3);
    this.add(l4);
  }

  /**
   * Create a new {@link java.awt.Frame Frame} and add a LabelExample
   * to it 
   */
  public static void main(String[] args) {
    Frame frame = new Frame("Label example");
    frame.add(new LabelExample());
    frame.pack();
    frame.setVisible(true);
  }

}
