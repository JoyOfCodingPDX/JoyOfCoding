package edu.pdx.cs410J.gwt.client.mvp;

import com.google.gwt.event.shared.HandlerManager;
import com.google.inject.Inject;

/**
 * Handles the logic for displaying an exception to the user
 */
public class ExceptionPresenter {
  public interface Display {
    void setMessage(String message);

    void setStackTrace(StackTraceElement[] trace);

    void show();
  }

  private final Display view;

  private final HandlerManager eventBus;

  @Inject
  public ExceptionPresenter(final Display view, HandlerManager hm) {
    this.view = view;
    this.eventBus = hm;

    this.eventBus.addHandler(ExceptionEvent.TYPE, new ExceptionEvent.Handler() {

      @Override
      public void onException(Throwable ex) {
        setException(ex);
      }
    });
  }

  private void setException(Throwable ex) {
    view.setMessage(ex.getMessage());
    view.setStackTrace(ex.getStackTrace());
    view.show();
  }
}
