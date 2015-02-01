package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class EmailCredentialsPresenter {
  private final EventBus bus;
  private final EmailCredentialsView view;

  public EmailCredentialsPresenter(EventBus bus, EmailCredentialsView view) {
    this.bus = bus;
    this.view = view;

    this.bus.register(this);
  }

  @Subscribe
  public void displayEmailCredentialsView(DownloadSubmissions event) {
    this.view.setVisible(true);
  }
}
