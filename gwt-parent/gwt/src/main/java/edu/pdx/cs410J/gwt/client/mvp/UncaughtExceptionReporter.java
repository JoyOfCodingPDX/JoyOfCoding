package edu.pdx.cs410J.gwt.client.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.inject.Inject;

/**
 * Puts an uncaught exception on the event bus so that the error dialog can be popped up
 */
public class UncaughtExceptionReporter implements GWT.UncaughtExceptionHandler {
  private final HandlerManager eventBus;

  @Inject
  public UncaughtExceptionReporter(HandlerManager eventBus) {
    this.eventBus = eventBus;
  }

  @Override
  public void onUncaughtException(Throwable throwable) {
    eventBus.fireEvent(new ExceptionEvent(throwable));
  }
}
