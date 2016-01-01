package edu.pdx.cs410J.airlinegwt.client;

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
 * A basic GWT class that makes sure that we can send an airline back from the server
 */
public class AirlineGwt implements EntryPoint {

  private final Alerter alerter;

  @VisibleForTesting
  Button button;

  public AirlineGwt() {
    this(new Alerter() {
      @Override
      public void alert(String message) {
        Window.alert(message);
      }
    });
  }

  @VisibleForTesting
  AirlineGwt(Alerter alerter) {
    this.alerter = alerter;

    addWidgets();
  }

  private void addWidgets() {
    button = new Button("Ping Server");
    button.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent clickEvent) {
        PingServiceAsync async = GWT.create(PingService.class);
        async.ping(new AsyncCallback<Airline>() {

          @Override
          public void onFailure(Throwable ex) {
            alerter.alert(ex.toString());
          }

          @Override
          public void onSuccess(Airline airline) {
            StringBuilder sb = new StringBuilder(airline.toString());
            Collection<Flight> flights = airline.getFlights();
            for (Flight flight : flights) {
              sb.append(flight);
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
