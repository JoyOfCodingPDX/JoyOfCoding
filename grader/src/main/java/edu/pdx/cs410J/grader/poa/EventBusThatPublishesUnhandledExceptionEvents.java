package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

public class EventBusThatPublishesUnhandledExceptionEvents extends EventBus {

  /** We have to use this hokey second event bus because the
   * SubscriberExceptionHandler (which posts the UnhandledExceptionEvent)
   * must be created in the constructor before the bus, itself, is
   * fully initialized and available for use. */
  private static final EventBus errorMessageBus = new EventBus("Error Messages");

  public EventBusThatPublishesUnhandledExceptionEvents() {
    super(new SubscriberExceptionHandler() {
      @Override
      public void handleException(Throwable throwable, SubscriberExceptionContext subscriberExceptionContext) {
        postUncaughtExceptionEvent(throwable);
      }
    });

    errorMessageBus.register(this);
  }

  @Subscribe
  public void postUncaughtExceptionEventToErrorMessageBus(UnhandledExceptionEvent event) {
    this.post(event);
  }

  private static void postUncaughtExceptionEvent(Throwable throwable) {
    errorMessageBus.post(new UnhandledExceptionEvent(throwable));
  }
}
