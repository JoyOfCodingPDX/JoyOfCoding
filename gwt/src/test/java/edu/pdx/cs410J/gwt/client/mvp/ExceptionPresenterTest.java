package edu.pdx.cs410J.gwt.client.mvp;

import com.google.gwt.event.shared.HandlerManager;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static edu.pdx.cs410J.gwt.client.mvp.ExceptionPresenter.*;

/**
 * Tests that an exception triggers the {@link ExceptionPresenter}
 */
public class ExceptionPresenterTest {

  /**
   * Tests that when an exception occurs in an {@link MvpCallback}, the exception view is displayed
   */
  @Test
  public void testExceptionViewDisplayed() {
    HandlerManager eventBus = new HandlerManager(null);

    MvpCallback callback = new MvpCallback<Void>(eventBus) {

      @Override
      public void onSuccess(Void aVoid) {
        fail("onSuccess should not be invoked");
      }
    };

    Display view = mock(Display.class);
    new ExceptionPresenter(view, eventBus);

    String message = "This is a message";
    Throwable ex = new Throwable(message);
    ex.fillInStackTrace();

    callback.onFailure(ex);

    verify(view).show();
    verify(view).setMessage(message);
    verify(view).setStackTrace(ex.getStackTrace());
  }
}
