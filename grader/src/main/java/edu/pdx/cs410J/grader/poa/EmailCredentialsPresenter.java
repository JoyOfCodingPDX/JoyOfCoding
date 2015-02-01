package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class EmailCredentialsPresenter {
  private final EventBus bus;
  private final EmailCredentialsView view;
  private String emailAddress;

  public EmailCredentialsPresenter(EventBus bus, EmailCredentialsView view) {
    this.bus = bus;
    this.view = view;

    this.bus.register(this);

    this.view.addEmailAddressValueListener(this::setEmailAddress);
  }

  @Subscribe
  public void displayEmailCredentialsView(DownloadSubmissions event) {
    this.view.setVisible(true);
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  private void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }
}
