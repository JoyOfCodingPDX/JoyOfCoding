package edu.pdx.cs410J.gwt.client.mvp;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event triggered when an exception is thrown, most often from an {@link MvpCallback} 
 */
public class ExceptionEvent extends GwtEvent<ExceptionEvent.Handler> {

  public static Type<Handler> TYPE = new Type<Handler>();

  private final Throwable exception;

  public interface Handler extends EventHandler {
    void onException(Throwable ex);
  }

  public ExceptionEvent(Throwable ex) {
    this.exception = ex;
  }

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.onException(this.exception);
  }
}
