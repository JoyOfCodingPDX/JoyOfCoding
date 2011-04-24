package edu.pdx.cs410J.gwt.client;

import com.google.common.annotations.VisibleForTesting;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Demonstrates GWT's remote services
 */
public class DivisionServiceExample extends Example {
  @VisibleForTesting
  TextBox dividend;

  @VisibleForTesting
  TextBox divisor;

  @VisibleForTesting
  TextBox quotient;

  @VisibleForTesting
  Button equals;

  public DivisionServiceExample() {
    super("Division Service");

    final DivisionServiceAsync service = DivisionService.Helper.getAsync();

    dividend = new TextBox();
    dividend.setVisibleLength(3);

    divisor = new TextBox();
    divisor.setVisibleLength(3);

    quotient = new TextBox();
    quotient.setVisibleLength(3);
    quotient.setReadOnly(true);

    equals = new Button("=");
    equals.addClickHandler(new ClickHandler() {
      public void onClick( ClickEvent event) {
        try {
          int d1 = Integer.parseInt(dividend.getText());
          int d2 = Integer.parseInt(divisor.getText());
          service.divide(d1, d2, new AsyncCallback<Integer>() {

            public void onFailure(Throwable ex) {
              ex = ex.getCause() != null ? ex.getCause() : ex;
              Window.alert(ex.toString());
            }

            public void onSuccess(Integer result) {
              quotient.setText(String.valueOf(result));
            }
          });

        } catch (NumberFormatException ex) {
          Window.alert("Not a number: " + ex.getMessage());
        }
      }
    });

    HorizontalPanel panel = new HorizontalPanel();
    panel.setSpacing(2);
    panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
    panel.add(dividend);
    panel.add(new Label("/"));
    panel.add(divisor);
    panel.add(equals);
    panel.add(quotient);

    add(panel);    
  }
}
