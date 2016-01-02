package edu.pdx.cs410J.apptbookgwt.client;

import com.google.common.annotations.VisibleForTesting;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import edu.pdx.cs410J.AbstractAppointment;

import java.util.Collection;

/**
 * A basic GWT class that makes sure that we can send an appointment book back from the server
 */
public class AppointmentBookGwt implements EntryPoint {
  private final Alerter alerter;

  @VisibleForTesting
  Button button;

  public AppointmentBookGwt() {
    this(new Alerter() {
      @Override
      public void alert(String message) {
        Window.alert(message);
      }
    });
  }

  @VisibleForTesting
  AppointmentBookGwt(Alerter alerter) {
    this.alerter = alerter;

    addWidgets();
  }

  private void addWidgets() {
    button = new Button("Ping Server");
    button.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent clickEvent) {
        PingServiceAsync async = GWT.create(PingService.class);
        async.ping(new AsyncCallback<AppointmentBook>() {

          @Override
          public void onFailure(Throwable ex) {
            alerter.alert(ex.toString());
          }

          @Override
          public void onSuccess(AppointmentBook airline) {
            StringBuilder sb = new StringBuilder(airline.toString());
            Collection<Appointment> flights = airline.getAppointments();
            for (Appointment flight : flights) {
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
