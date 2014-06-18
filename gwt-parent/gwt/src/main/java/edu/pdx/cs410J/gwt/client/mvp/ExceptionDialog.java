package edu.pdx.cs410J.gwt.client.mvp;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

/**
 * A dialog that is popped up in response to an exception being thrown
 */
public class ExceptionDialog extends DialogBox implements ExceptionPresenter.Display {
  private final Label message = new Label();
  private final TextArea details = new TextArea();

  public ExceptionDialog() {
    setText("An error has occurred");
    setAutoHideEnabled(false);
    setModal(true);

    DockPanel panel = new DockPanel();
    panel.add(message, DockPanel.NORTH);

    details.setReadOnly(true);
    details.setCharacterWidth(80);
    details.setVisibleLines(30);
    panel.add(details, DockPanel.CENTER);

    Button ok = new Button("OK");
    ok.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent clickEvent) {
        ExceptionDialog.this.hide();
      }
    });
    panel.add(ok, DockPanel.SOUTH);

    setWidget(panel);
  }

  @Override
  public void setMessage(String message) {
    this.message.setText(message);
  }

  @Override
  public void setStackTrace(StackTraceElement[] trace) {
    StringBuilder sb = new StringBuilder();
    for (StackTraceElement line : trace) {
      sb.append("  at ");
      sb.append(line.getClassName()).append(".").append(line.getMethodName());
      sb.append("(").append(line.getFileName()).append(":").append(line.getLineNumber()).append(")");
      sb.append("\n");
    }

    this.details.setText(sb.toString());
  }

  private boolean noRecursion;  // Hack?

  @Override
  public void show() {
    if (noRecursion) {
      noRecursion = false;
      center();
      noRecursion = true;
    }
    super.show();
  }
}
