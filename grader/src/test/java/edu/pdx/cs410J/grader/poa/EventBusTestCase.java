package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.junit.After;
import org.junit.Before;

import java.util.function.Consumer;

public class EventBusTestCase {
  protected EventBus bus;
  protected Consumer<UnhandledExceptionEvent> unhandledExceptionHandler;

  @Before
  public void setUp() {
    unhandledExceptionHandler = this::failTestWhenUnhandledExceptionEncountered;
    bus = new EventBusThatPublishesUnhandledExceptionEvents();
    bus.register(this);
  }

  @After
  public void tearDown() {
    if (bus != null) {
      bus.unregister(this);
    }
  }

  @Subscribe
  public void handleUnhandledException(UnhandledExceptionEvent event) {
    unhandledExceptionHandler.accept(event);
  }

  private void failTestWhenUnhandledExceptionEncountered(UnhandledExceptionEvent event) {
    throw new AssertionError("Unhandled Exception on event bus", event.getUnhandledException());
  }

  protected void doNotFailTestWhenUnhandledExceptionEncountered(UnhandledExceptionEvent event) {

  }

}
