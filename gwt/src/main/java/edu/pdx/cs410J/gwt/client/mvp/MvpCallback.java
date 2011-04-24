package edu.pdx.cs410J.gwt.client.mvp;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * An abstract superclass of {@link AsyncCallback} that handles exceptions in a consistent way
 */
public abstract class MvpCallback<T> implements AsyncCallback<T> {
  protected final HandlerManager eventBus;

  public MvpCallback(HandlerManager eventBus) {
    this.eventBus = eventBus;
  }

  @Override
  public void onFailure(Throwable throwable) {
    eventBus.fireEvent(new ExceptionEvent(throwable));
  }
}
