package edu.pdx.cs410J.grader.poa;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StatusMessagePresenterTest extends EventBusTestCase {

  private StatusMessageView view;
  private StatusMessagePresenter presenter;

  @Override
  public void setUp() {
    super.setUp();

    this.view = mock(StatusMessageView.class);
    this.presenter = new StatusMessagePresenter(bus, view);
  }

  @Test
  public void statusMessageSetsStatusInView() {
    String message = "This is a message";
    bus.post(new StatusMessage(message));

    verify(this.view).setStatusMessage(message);
  }
}
