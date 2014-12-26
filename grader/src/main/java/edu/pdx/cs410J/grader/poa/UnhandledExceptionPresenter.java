package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class UnhandledExceptionPresenter {
  private final UnhandledExceptionView view;

  public UnhandledExceptionPresenter(EventBus bus, UnhandledExceptionView view) {
    this.view = view;
    bus.register(this);
  }

  @Subscribe
  public void populateViewFrom(UnhandledExceptionEvent event) throws IOException {
    Throwable exception = event.getUnhandledException();
    this.view.setExceptionMessage(exception.getMessage());
    this.view.setExceptionDetails(getStackTrace(exception));
    this.view.show();
  }

  private String getStackTrace(Throwable exception) throws IOException {
    try (StringWriter sw = new StringWriter()) {
      exception.printStackTrace(new PrintWriter(sw));
      sw.flush();
      return sw.toString();
    }
  }
}
