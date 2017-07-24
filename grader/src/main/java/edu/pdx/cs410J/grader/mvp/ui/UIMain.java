package edu.pdx.cs410J.grader.mvp.ui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;

public class UIMain {
  protected final TopLevelJFrame parent;

  public UIMain(TopLevelJFrame parent) {
    this.parent = parent;
  }

  private static void printStackTraceForUncaughtException(Throwable e, Logger logger) {
    e.fillInStackTrace();
    e.printStackTrace(System.err);
    logger.error("Uncaught exception", e);
  }

  protected static void logAllEventsOnBusAtDebugLevel(EventBus bus, Logger logger) {
    bus.register(new Object() {
      @Subscribe
      public void logEvent(Object event) {
        logger.debug("Event " + event);
      }
    });
  }

  protected static void logAllUncaughtExceptions(Logger logger) {
    Thread.currentThread().setUncaughtExceptionHandler((t, e) -> printStackTraceForUncaughtException(e, logger));
  }

  protected void display() {
    parent.pack();
    parent.setVisible(true);
  }
}
