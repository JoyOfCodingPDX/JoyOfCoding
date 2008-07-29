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

    Label status = new Label();
    dock.add(status, DockPanel.SOUTH);

    FocusPanel panel = new FocusPanel(new Label("Mouse me!"));
    panel.setSize("200px", "200px");
    panel.addFocusListener(new FocusListener() {

      public void onFocus(Widget widget) {
        label.setText("Got focus");
      }

      public void onLostFocus(Widget widget) {
        label.setText("Lost focus");
      }
    });
    panel.addKeyboardListener(new KeyboardListener() {

      public void onKeyDown(Widget widget, char c, int modifier) {
        label.setText("Key down " + getModifierString(modifier) + c);
      }

      public void onKeyPress(Widget widget, char c, int modifier) {
        label.setText("Key press " + getModifierString(modifier) + c);
      }

      public void onKeyUp(Widget widget, char c, int modifier) {
        label.setText("Key up " + getModifierString(modifier) + c);
      }
    });
    dock.add(panel, DockPanel.CENTER);
    dock.setCellHeight(panel, "200px");
    dock.setCellWidth(panel, "200px");

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
      sb.append("SHIFT");
    }

    return sb.toString().trim();
  }
}
