package edu.pdx.cs410J.grader.poa;

import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class UnhandledExceptionPresenterTest extends EventBusTestCase {

  private UnhandledExceptionView view;

  @Override
  public void setUp() {
    super.setUp();
    this.unhandledExceptionHandler = this::doNotFailTestWhenUnhandledExceptionEncountered;

    this.view = mock(UnhandledExceptionView.class);
    new UnhandledExceptionPresenter(this.bus, this.view);
  }

  @Test
  public void viewUpdatedOnUnhandledExceptionEvent() throws IOException {
    String message = "This is an exception message";
    Throwable exception = new IllegalStateException(message).fillInStackTrace();

    this.bus.post(new UnhandledExceptionEvent(exception));

    verify(this.view).setExceptionMessage(message);
    verify(this.view).setExceptionDetails(getStackTrace(exception));
    verify(this.view).displayView();
  }

  private String getStackTrace(Throwable exception) throws IOException {
    try (StringWriter sw = new StringWriter()) {
      exception.printStackTrace(new PrintWriter(sw));
      sw.flush();
      return sw.toString();
    }
  }
}
