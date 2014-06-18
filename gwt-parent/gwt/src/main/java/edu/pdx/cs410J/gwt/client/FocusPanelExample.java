package edu.pdx.cs410J.gwt.client;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;

/**
 * Demonstrates several of GWT's low-level event Handlers
 */
public class FocusPanelExample extends Example {
  public FocusPanelExample() {
    super("Lots of Handlers");

    DockPanel dock = new DockPanel();

    final Label label = new Label("Move the mouse below");
    dock.add(label, DockPanel.NORTH);
    dock.setCellHorizontalAlignment(label, DockPanel.ALIGN_CENTER);

    final Label status = new Label("Status");
    dock.add(status, DockPanel.SOUTH);
    dock.setCellHorizontalAlignment(status, DockPanel.ALIGN_CENTER);

    FocusPanel panel = new FocusPanel(new Label("Mouse me!"));
    panel.addFocusHandler(new FocusHandler() {

      public void onFocus( FocusEvent event) {
        status.setText("Got focus");
      }

      public void onLostFocus(FocusEvent event) {
        status.setText("Lost focus");
      }
    });
      KeyEventHandler handler = new KeyEventHandler( status );
      panel.addKeyDownHandler( handler );
      panel.addKeyPressHandler( handler );
      panel.addKeyUpHandler( handler );

    panel.addMouseWheelHandler(new MouseWheelHandler() {
      public void onMouseWheel(MouseWheelEvent event) {
        String dir = event.isNorth() ? "North" : "South";
        status.setText("Mouse wheel " + dir + " at " + event.getDeltaY());
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
     * Handles all kinds of keyboard events
     */
    static class KeyEventHandler implements KeyDownHandler, KeyPressHandler, KeyUpHandler {
        private Label status;

        public KeyEventHandler( Label status )
        {
            this.status = status;
        }

        public void onKeyDown(KeyDownEvent event) {
          status.setText("Key down " + getModifierString(event) + describe(event.getNativeKeyCode()));
        }

        public void onKeyPress(KeyPressEvent event) {
          status.setText("Key press " + getModifierString(event) + event.getCharCode());
        }

        public void onKeyUp(KeyUpEvent event) {
          status.setText("Key up " + getModifierString(event) + describe( event.getNativeKeyCode() ));
        }

        /**
         * Returns a description of the given native key code associated with a {@link KeyEvent}
         */
        private String describe( int nativeKeyCode )
        {
            switch ( nativeKeyCode ) {
                case KeyCodes.KEY_ALT:
                    return "ALT";
                case KeyCodes.KEY_BACKSPACE:
                    return "BACKSPACE";
                case KeyCodes.KEY_CTRL:
                    return "CRTL";
                case KeyCodes.KEY_DELETE:
                    return "DELETE";
                case KeyCodes.KEY_DOWN:
                    return "DOWN";
                case KeyCodes.KEY_END:
                    return "END";
                case KeyCodes.KEY_ENTER:
                    return "ENTER";
                case KeyCodes.KEY_ESCAPE:
                    return "ESCAPE";
                case KeyCodes.KEY_HOME:
                    return "HOME";
                case KeyCodes.KEY_LEFT:
                    return "LEFT";
                case KeyCodes.KEY_PAGEDOWN:
                    return "PAGEDOWN";
                case KeyCodes.KEY_PAGEUP:
                    return "PAGEUP";
                case KeyCodes.KEY_RIGHT:
                    return "RIGHT";
                case KeyCodes.KEY_SHIFT:
                    return "SHIFT";
                case KeyCodes.KEY_TAB:
                    return "TAB";
                case KeyCodes.KEY_UP:
                    return "UP";
                default:
                    return Character.toString( (char) nativeKeyCode );
            }
        }

        /**
         * Returns a description of the given {@link KeyEvent} modifier
         */
        private String getModifierString(KeyEvent event) {
          StringBuilder sb = new StringBuilder();

          if (event.isAltKeyDown()) {
            sb.append("ALT ");
          }

          if (event.isControlKeyDown()) {
            sb.append("CTRL ");
          }

          if (event.isMetaKeyDown()) {
            sb.append("META ");
          }

          if (event.isShiftKeyDown()) {
            sb.append("SHIFT ");
          }

          return sb.toString();
        }

    }


}
