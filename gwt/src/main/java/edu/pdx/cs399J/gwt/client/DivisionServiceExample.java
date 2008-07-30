package edu.pdx.cs399J.gwt.client;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.Window;
import com.google.gwt.core.client.GWT;

/**
 * Demonstrates GWT's remote services
 */
public class DivisionServiceExample extends Example {
  public DivisionServiceExample() {
    super("Division Service");

    final DivisionServiceAsync service = GWT.create(DivisionService.class);
    ServiceDefTarget endpoint = (ServiceDefTarget) service;
    String url = GWT.getModuleBaseURL() + "division";
    endpoint.setServiceEntryPoint(url);

    final TextBox dividend = new TextBox();
    dividend.setVisibleLength(3);
    final TextBox divisor = new TextBox();
    divisor.setVisibleLength(3);
    final TextBox quotient = new TextBox();
    quotient.setVisibleLength(3);
    quotient.setReadOnly(true);

    Button equals = new Button("=");
    equals.addClickListener(new ClickListener() {
      public void onClick(Widget widget) {
        try {
          int d1 = Integer.parseInt(dividend.getText());
          int d2 = Integer.parseInt(divisor.getText());
          service.divide(d1, d2, new AsyncCallback() {

            public void onFailure(Throwable ex) {
              ex = ex.getCause() != null ? ex.getCause() : ex;
              Window.alert(ex.toString());
            }

            public void onSuccess(Object o) {
              int result = (Integer) o;
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
