package edu.pdx.cs410J.gwt.client.mvp;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import edu.pdx.cs410J.gwt.client.DivisionServiceAsync;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * Tests the functionality of the {@link DivisionPresenter}}
 *
 * @author David Whitlock
 * @since Summer 2010
 */
public class DivisionPresenterTest {
  /**
   * Captures the handler passed to the view from the presenter
   */
  @Captor
  private ArgumentCaptor<ValueChangeHandler<String>> captor;

  /**
   * Captures the handler passed to the view from the presenter
   */
  @Captor
  private ArgumentCaptor<ValueChangeHandler<String>> captor2;

  /**
   * Captures the callback set to the division service
   */
  @Captor
  private ArgumentCaptor<AsyncCallback<Integer>> callbackCaptor;
  private HandlerManager eventBus;

  @Before
  public void initializeMockito() {
    MockitoAnnotations.initMocks(this);
  }

  @Before
  public void initializeDependencies() {
    eventBus = new HandlerManager(null);
  }

  /**
   * Tests that the view is initialized with the appropriate values
   */
  @Test
  public void testInitialView() {
    DivisionPresenter.Display view = mock(DivisionPresenter.Display.class);

    new DivisionPresenter(view, null, eventBus);

    verify(view).setDivisionEnabled(false);
  }

  /**
   * Tests that the view is notified when a valid dividend is provided
   */
  @Test
  public void testValidDividend() {
    DivisionPresenter.Display view = mock(DivisionPresenter.Display.class);

    DivisionPresenter presenter = new DivisionPresenter(view, null, eventBus);

    verify(view).setDividendChangeHandler(captor.capture());

    int value = 123;
    fireValueChangeEvent(String.valueOf(value), captor.getValue());

    verify(view).setDividendValid(true);
    verify(view, times(2)).setDivisionEnabled(false);

    Integer dividend = presenter.getDividend();
    assertNotNull(dividend);
    assertEquals(value, dividend.intValue());

    verify(view).setQuotient("");
  }

  /**
   * Tests that the view is notified when an invalid dividend is provided
   */
  @Test
  public void testInvalidDividend() {
    DivisionPresenter.Display view = mock(DivisionPresenter.Display.class);

    DivisionPresenter presenter = new DivisionPresenter(view, null, eventBus);

    verify(view).setDividendChangeHandler(captor.capture());

    fireValueChangeEvent("fred", captor.getValue());

    verify(view).setDividendValid(false);
    verify(view, times(2)).setDivisionEnabled(false);

    assertNull(presenter.getDividend());

    verify(view).setQuotient("");
  }

  /**
   * Tests that the view is notified when a valid dividend is provided
   */
  @Test
  public void testValidDivisor() {
    DivisionPresenter.Display view = mock(DivisionPresenter.Display.class);

    DivisionPresenter presenter = new DivisionPresenter(view, null, eventBus);

    verify(view).setDivisorChangeHandler(captor.capture());

    int value = 123;
    fireValueChangeEvent(String.valueOf(value), captor.getValue());

    verify(view).setDivisorValid(true);
    verify(view, times(2)).setDivisionEnabled(false);

    Integer dividend = presenter.getDivisor();
    assertNotNull(dividend);
    assertEquals(value, dividend.intValue());

    verify(view).setQuotient("");
  }

  /**
   * Tests that the view is notified when an invalid dividend is provided
   */
  @Test
  public void testInvalidDivisor() {
    DivisionPresenter.Display view = mock(DivisionPresenter.Display.class);

    DivisionPresenter presenter = new DivisionPresenter(view, null, eventBus);

    verify(view).setDivisorChangeHandler(captor.capture());

    fireValueChangeEvent("fred", captor.getValue());

    verify(view).setDivisorValid(false);
    verify(view, times(2)).setDivisionEnabled(false);

    assertNull(presenter.getDivisor());

    verify(view).setQuotient("");
  }

  /**
   * Tests that a valid dividend and divisor enables division
   */
  @Test
  public void testValidDivisorAndDividend() {
    DivisionPresenter.Display view = mock(DivisionPresenter.Display.class);

    new DivisionPresenter(view, null, eventBus);

    verify(view).setDivisorChangeHandler(captor.capture());
    verify(view).setDividendChangeHandler(captor2.capture());

    fireValueChangeEvent(String.valueOf(123), captor.getValue());
    fireValueChangeEvent(String.valueOf(234), captor2.getValue());

    verify(view).setDivisionEnabled(true);

    verify(view, times(2)).setQuotient("");
  }

  /**
   * Tests that the division service is invoked appropriately
   */
  @Test
  public void testServiceInvocation() {
    DivisionPresenter.Display view = mock(DivisionPresenter.Display.class);
    DivisionServiceAsync service = mock(DivisionServiceAsync.class);

    DivisionPresenter presenter = new DivisionPresenter(view, service, eventBus);
    ArgumentCaptor<ClickHandler> captor = ArgumentCaptor.forClass(ClickHandler.class);
    verify(view).setDivisionClickHandler(captor.capture());

    int divisor = 123;
    presenter.setDivisor(divisor);

    int dividend = 234;
    presenter.setDividend(dividend);

    fireClickEvent(captor.getValue());

    verify(service).divide(eq(dividend), eq(divisor), any(AsyncCallback.class));
  }

  /**
   * Tests that the quotient is set appropriately
   */
  @Test
  public void testQuotientIsSet() {
    DivisionPresenter.Display view = mock(DivisionPresenter.Display.class);
    DivisionServiceAsync service = mock(DivisionServiceAsync.class);

    DivisionPresenter presenter = new DivisionPresenter(view, service, eventBus);
    ArgumentCaptor<ClickHandler> clickCaptor = ArgumentCaptor.forClass(ClickHandler.class);
    verify(view).setDivisionClickHandler(clickCaptor.capture());

    presenter.setDivisor(123);
    presenter.setDividend(234);

    fireClickEvent(clickCaptor.getValue());

    verify(service).divide(anyInt(), anyInt(), callbackCaptor.capture());

    Integer quotient = 456;
    callbackCaptor.getValue().onSuccess(quotient);

    verify(view).setQuotient(String.valueOf(quotient));
    verify(view, never()).setErrorMessage(any(String.class));
  }

  /**
   * Tests that the error message is set appropriately when the remote service fails
   */
  @SuppressWarnings({"ThrowableInstanceNeverThrown"})
  @Test
  public void testErrorMessageIsSet() {
    DivisionPresenter.Display view = mock(DivisionPresenter.Display.class);
    DivisionServiceAsync service = mock(DivisionServiceAsync.class);

    DivisionPresenter presenter = new DivisionPresenter(view, service, eventBus);
    ArgumentCaptor<ClickHandler> clickCaptor = ArgumentCaptor.forClass(ClickHandler.class);
    verify(view).setDivisionClickHandler(clickCaptor.capture());

    presenter.setDivisor(123);
    presenter.setDividend(234);

    fireClickEvent(clickCaptor.getValue());

    verify(service).divide(anyInt(), anyInt(), callbackCaptor.capture());

    final String message = "This is an error message";
    callbackCaptor.getValue().onFailure(new RuntimeException() {
      @Override
      public String toString() {
        return message;
      }
    });

    verify(view, never()).setQuotient(any(String.class));
    verify(view).setErrorMessage(message);
  }

  /**
   * Fires a {@link ClickEvent} to a given handler
   *
   * @param handler The handler that wlll receive the event
   */
  private void fireClickEvent(ClickHandler handler) {
    // Firing a ClickEvent requires a NativeEvent which is only available with GWT.create(), not in JUnit tests
    handler.onClick(mock(ClickEvent.class));
  }

  /**
   * Fires a {@link ValueChangeEvent} to a given handler
   *
   * @param value   The value that is changing
   * @param handler The handler that will receive the event
   */
  private <T> void fireValueChangeEvent(T value, ValueChangeHandler<T> handler) {
    HasValueChangeHandlers<T> mock = new MockHasValueChangeHandlers<T>();
    mock.addValueChangeHandler(handler);
    ValueChangeEvent.fire(mock, value);
  }

  private abstract class MockHasHandlers implements HasHandlers {
    protected final HandlerManager manager;

    private MockHasHandlers() {
      manager = new HandlerManager(this);
    }

    public void fireEvent(GwtEvent<?> event) {
      manager.fireEvent(event);
    }
  }

  /**
   * A mock that takes care of the handling and firing events
   *
   * @param <T>
   */
  private class MockHasValueChangeHandlers<T> extends MockHasHandlers implements HasValueChangeHandlers<T> {
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
      return manager.addHandler(ValueChangeEvent.getType(), handler);
    }

  }
}
