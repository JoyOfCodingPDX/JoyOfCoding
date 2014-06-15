package edu.pdx.cs410J.gwt.client;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;

/**
 * Pops up a GWT {@link DialogBox}
 */
public class DialogBoxExample extends Example {
  public DialogBoxExample() {
    super("Dialog Box");

    Button autoHide = new Button("Auto-hide dialog");
    autoHide.addClickHandler(new ClickHandler() {
        public void onClick( ClickEvent clickEvent )
        {
            createDialogBox(true, false).show();
        }
    });
    add(autoHide);

    Button modal = new Button("Modal dialog");
    modal.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        createDialogBox(false, true).show();
      }
    });
    add(modal);
  }

  private DialogBox createDialogBox(boolean autoHide, boolean modal) {
    final DialogBox box = new DialogBox(autoHide, modal);
    box.setText("Auto-hide dialog");

    DockPanel panel = new DockPanel();
    panel.add(new Label("Click outside me"), DockPanel.CENTER);
    panel.add(new Button("Close", new ClickHandler() {
      public void onClick(ClickEvent event) {
        box.hide();
      }
    }), DockPanel.SOUTH);
    box.setWidget(panel);
    box.center();
    return box;
  }
}
