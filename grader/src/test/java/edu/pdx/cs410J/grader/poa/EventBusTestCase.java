package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.function.Consumer;

public class EventBusTestCase {
  protected EventBus bus;
  protected Consumer<UnhandledExceptionEvent> unhandledExceptionHandler;

  @BeforeEach
  public void setUp() {
    unhandledExceptionHandler = this::failTestWhenUnhandledExceptionEncountered;
    bus = new EventBusThatPublishesUnhandledExceptionEvents();
    bus.register(this);
  }

  @AfterEach
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
