package edu.pdx.cs410G.awt;

import java.awt.Frame;
import java.awt.List;
import java.awt.Panel;

/**
 * This simple program demonstrates the AWT's {@link List} widget.
 *
 * @author David Whitlock
 * @since Winter 2005
 */
public class ListExample extends Panel {

  /**
   * Creates a new <code>ListExample</code> that contains an AWT
   * <code>List</code>.
   */
  public ListExample() {
    List list = new List(4);
    list.add("Monday");
    list.add("Tuesday");
    list.add("Wednesday");
    list.add("Thursday");
    list.add("Friday");
    list.add("Saturday");
    list.add("Sunday", 0);
    list.setMultipleMode(true);
    this.add(list);
  }

  public static void main(String[] args) {
    Frame frame = new Frame("List example");
    frame.add(new ListExample());
    frame.pack();
    frame.setVisible(true);
  }

}
