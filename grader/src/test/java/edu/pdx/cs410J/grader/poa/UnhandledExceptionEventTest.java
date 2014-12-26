package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class UnhandledExceptionEventTest {

  @Test
  public void unhandledExceptionInEventThreadCallsSubscriberExceptionHandler() {
    SubscriberExceptionHandler handler = mock(SubscriberExceptionHandler.class);
    EventBus bus = new EventBus(handler);

    final IllegalStateException exception = new IllegalStateException("Excepted Unhandled Exception");
    bus.register(new Object() {
      @Subscribe
      public void throwUnhandledException(TriggerUnhandledException event) {
        throw exception;
      }
    });

    bus.post(new TriggerUnhandledException());

    verify(handler).handleException(eq(exception), any(SubscriberExceptionContext.class));
  }

  private class TriggerUnhandledException {
  }

  @Test
  public void unhandledExceptionInEventThreadFiresUnhandledExceptionEvent() {
    EventBusThatPublishesUnhandledExceptionEvents bus = new EventBusThatPublishesUnhandledExceptionEvents();

    UnhandledExceptionEventHandler handler = mock(UnhandledExceptionEventHandler.class);
    bus.register(handler);

    final IllegalStateException exception = new IllegalStateException("Expected Unhandled Exception");
    bus.register(new Object() {
      @Subscribe
      public void throwUnhandledException(TriggerUnhandledException event) {
        throw exception;
      }
    });

    bus.post(new TriggerUnhandledException());

    ArgumentCaptor<UnhandledExceptionEvent> event = ArgumentCaptor.forClass(UnhandledExceptionEvent.class);
    verify(handler).handle(event.capture());

    assertThat(event.getValue().getUnhandledException(), equalTo(exception));
  }

  private interface UnhandledExceptionEventHandler {
    @Subscribe
    void handle(UnhandledExceptionEvent event);
  }
}
