package edu.pdx.cs410J.phonebillgwt.client;

import com.google.common.annotations.VisibleForTesting;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.Collection;

/**
 * A basic GWT class that makes sure that we can send an Phone Bill back from the server
 */
public class PhoneBillGwt implements EntryPoint {
  private final Alerter alerter;

  @VisibleForTesting
  Button button;

  public PhoneBillGwt() {
    this(new Alerter() {
      @Override
      public void alert(String message) {
        Window.alert(message);
      }
    });
  }

  @VisibleForTesting
  PhoneBillGwt(Alerter alerter) {
    this.alerter = alerter;

    addWidgets();
  }

  private void addWidgets() {
    button = new Button("Ping Server");
    button.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent clickEvent) {
        PingServiceAsync async = GWT.create(PingService.class);
        async.ping(new AsyncCallback<PhoneBill>() {

          @Override
          public void onFailure(Throwable ex) {
            alerter.alert(ex.toString());
          }

          @Override
          public void onSuccess(PhoneBill bill) {
            StringBuilder sb = new StringBuilder(bill.toString());
            Collection<PhoneCall> calls = bill.getPhoneCalls();
            for (PhoneCall call : calls) {
              sb.append(call);
              sb.append("\n");
            }
            alerter.alert(sb.toString());
          }
        });
      }
    });
  }

  @Override
  public void onModuleLoad() {
    RootPanel rootPanel = RootPanel.get();
    rootPanel.add(button);
  }

  @VisibleForTesting
  interface Alerter {
    void alert(String message);
  }

}
