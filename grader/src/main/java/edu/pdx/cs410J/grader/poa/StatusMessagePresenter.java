package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class StatusMessagePresenter {
  private final EventBus bus;
  private final StatusMessageView view;

  @Inject
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
