package edu.pdx.cs399J.gwt.client;

import com.google.gwt.user.client.ui.*;

/**
 * Demonstrates several of GWT's low-level event listeners
 */
public class FocusPanelExample extends Example {
  public FocusPanelExample() {
    super("Lot's of Listeners");

    DockPanel dock = new DockPanel();

    final Label label = new Label("Move the mouse below");
    dock.add(label, DockPanel.NORTH);
    dock.setCellHorizontalAlignment(label, DockPanel.ALIGN_CENTER);

    final Label status = new Label("Status");
    dock.add(status, DockPanel.SOUTH);
    dock.setCellHorizontalAlignment(status, DockPanel.ALIGN_CENTER);

    FocusPanel panel = new FocusPanel(new Label("Mouse me!"));
    panel.addFocusListener(new FocusListener() {

      public void onFocus(Widget widget) {
        status.setText("Got focus");
      }

      public void onLostFocus(Widget widget) {
        status.setText("Lost focus");
      }
    });
    panel.addKeyboardListener(new KeyboardListener() {

      public void onKeyDown(Widget widget, char c, int modifier) {
        status.setText("Key down " + getModifierString(modifier) + c);
      }

      public void onKeyPress(Widget widget, char c, int modifier) {
        status.setText("Key press " + getModifierString(modifier) + c);
      }

      public void onKeyUp(Widget widget, char c, int modifier) {
        status.setText("Key up " + getModifierString(modifier) + c);
      }
    });
    panel.addMouseWheelListener(new MouseWheelListener() {
      public void onMouseWheel(Widget widget, MouseWheelVelocity velocity) {
        String dir = velocity.isNorth() ? "North" : "South";
        status.setText("Mouse wheel " + dir + " at " + velocity.getDeltaY());
      }
    });

    dock.add(panel, DockPanel.CENTER);
    dock.setCellHeight(panel, "200px");
    dock.setCellWidth(panel, "200px");
    dock.setCellHorizontalAlignment(panel, DockPanel.ALIGN_CENTER);
    dock.setCellVerticalAlignment(panel, DockPanel.ALIGN_MIDDLE);

    add(dock);
  }

  /**
   * Returns a description of the given {@link KeyboardListener} modifier
   */
  private String getModifierString(int modifier) {
    StringBuilder sb = new StringBuilder();

    if ((modifier & KeyboardListener.MODIFIER_ALT) != 0) {
      sb.append("ALT ");
    }

    if ((modifier & KeyboardListener.MODIFIER_CTRL) != 0) {
      sb.append("CTRL ");
    }

    if ((modifier & KeyboardListener.MODIFIER_META) != 0) {
      sb.append("META ");
    }

    if ((modifier & KeyboardListener.MODIFIER_SHIFT) != 0) {
      sb.append("SHIFT ");
    }

    return sb.toString();
  }
}
