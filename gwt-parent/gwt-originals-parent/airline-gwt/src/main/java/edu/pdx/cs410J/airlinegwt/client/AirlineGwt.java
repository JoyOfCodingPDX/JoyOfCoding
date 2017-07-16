package edu.pdx.cs410J.airlinegwt.client;

import com.google.common.annotations.VisibleForTesting;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.Collection;

/**
 * A basic GWT class that makes sure that we can send an airline back from the server
 */
public class AirlineGwt implements EntryPoint {

  private final Alerter alerter;
  private final AirlineServiceAsync airlineService;

  @VisibleForTesting
  Button showAirlineButton;

  @VisibleForTesting
  Button showUndeclaredExceptionButton;

  @VisibleForTesting
  Button showDeclaredExceptionButton;

  @VisibleForTesting
  Button showClientSideExceptionButton;

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
    airlineService = GWT.create(AirlineService.class);
  }

  private void alertOnException(Throwable throwable) {
    Throwable unwrapped = unwrapUmbrellaException(throwable);
    this.alerter.alert(unwrapped.toString());
  }

  private Throwable unwrapUmbrellaException(Throwable throwable) {
    if (throwable instanceof UmbrellaException) {
      UmbrellaException umbrella = (UmbrellaException) throwable;
      if (umbrella.getCauses().size() == 1) {
        return unwrapUmbrellaException(umbrella.getCauses().iterator().next());
      }

    }

    return throwable;
  }

  private void addWidgets(VerticalPanel panel) {
    showAirlineButton = new Button("Show Airline");
    showAirlineButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent clickEvent) {
        showAirline();
      }
    });

    showUndeclaredExceptionButton = new Button("Show undeclared exception");
    showUndeclaredExceptionButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent clickEvent) {
        showUndeclaredException();
      }
    });

    showDeclaredExceptionButton = new Button("Show declared exception");
    showDeclaredExceptionButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent clickEvent) {
        showDeclaredException();
      }
    });

    showClientSideExceptionButton= new Button("Show client-side exception");
    showClientSideExceptionButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent clickEvent) {
        throwClientSideException();
      }
    });

    panel.add(showAirlineButton);
    panel.add(showUndeclaredExceptionButton);
    panel.add(showDeclaredExceptionButton);
    panel.add(showClientSideExceptionButton);
  }

  private void throwClientSideException() {
    throw new IllegalStateException("Expected exception on the client side");
  }

  private void showUndeclaredException() {
    airlineService.throwUndeclaredException(new AsyncCallback<Void>() {
      @Override
      public void onFailure(Throwable ex) {
        alertOnException(ex);
      }

      @Override
      public void onSuccess(Void aVoid) {
        alerter.alert("This shouldn't happen");
      }
    });
  }

  private void showDeclaredException() {
    airlineService.throwDeclaredException(new AsyncCallback<Void>() {
      @Override
      public void onFailure(Throwable ex) {
        alertOnException(ex);
      }

      @Override
      public void onSuccess(Void aVoid) {
        alerter.alert("This shouldn't happen");
      }
    });
  }

  private void showAirline() {
    airlineService.getAirline(new AsyncCallback<Airline>() {

      @Override
      public void onFailure(Throwable ex) {
        alertOnException(ex);
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

  @Override
  public void onModuleLoad() {
    setUpUncaughtExceptionHandler();

    // The UncaughtExceptionHandler won't catch exceptions during module load
    // So, you have to set up the UI after module load...
    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
      @Override
      public void execute() {
        setupUI();
      }
    });

  }

  private void setupUI() {
    RootPanel rootPanel = RootPanel.get();
    VerticalPanel panel = new VerticalPanel();
    rootPanel.add(panel);

    addWidgets(panel);
  }

  private void setUpUncaughtExceptionHandler() {
    GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
      @Override
      public void onUncaughtException(Throwable throwable) {
        alertOnException(throwable);
      }
    });
  }

  @VisibleForTesting
  interface Alerter {
    void alert(String message);
  }
}
