package edu.pdx.cs399J.gwt.client;

import com.google.gwt.user.client.ui.*;

/**
 * Pops up a GWT {@link DialogBox}
 */
public class DialogBoxExample extends Example {
  public DialogBoxExample() {
    super("Dialog Box");

    Button autoHide = new Button("Auto-hide dialog");
    autoHide.addClickListener(new ClickListener() {

      public void onClick(Widget widget) {
        createDialogBox(true, false).show();
      }
    });
    add(autoHide);

    Button modal = new Button("Modal dialog");
    modal.addClickListener(new ClickListener() {

      public void onClick(Widget widget) {
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
    panel.add(new Button("Close", new ClickListener() {
      public void onClick(Widget widget) {
        box.hide();
      }
    }), DockPanel.SOUTH);
    box.setWidget(panel);
    box.center();
    return box;
  }
}
