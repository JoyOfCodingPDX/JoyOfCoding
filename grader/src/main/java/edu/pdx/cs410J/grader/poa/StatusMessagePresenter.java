package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class StatusMessagePresenter {
  private final EventBus bus;
  private final StatusMessageView view;

  public StatusMessagePresenter(EventBus bus, StatusMessageView view) {
    this.bus = bus;
    this.view = view;

    this.bus.register(this);
  }

  @Subscribe
  public void handleStatusMessage(StatusMessage message) {
    this.view.setStatusMessage(message.getStatusMessage());
  }
}
