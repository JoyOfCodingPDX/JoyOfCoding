package edu.pdx.cs399J.gui;

import java.awt.*;

/**
 * This program simply pops up a {@link Panel} with a {@link Button}
 * in it.
 */
public class ButtonExample extends Panel {
  public ButtonExample() {
    Button ok = new Button("OK");
    Button cancel = new Button("Cancel");
    this.add(ok);
    this.add(cancel);
  }

  public static void main(String[] args) {
    Frame frame = new Frame("Button example");
    frame.add(new ButtonExample());
    frame.pack();
    frame.setVisible(true);
  }

}
