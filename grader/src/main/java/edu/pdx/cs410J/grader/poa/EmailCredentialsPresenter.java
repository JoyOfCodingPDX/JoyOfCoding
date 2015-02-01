package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class EmailCredentialsPresenter {
  private final EventBus bus;
  private final EmailCredentialsView view;
  private String emailAddress;
  private String password;

  public EmailCredentialsPresenter(EventBus bus, EmailCredentialsView view) {
    this.bus = bus;
    this.view = view;

    this.bus.register(this);

    this.view.addEmailAddressValueListener(this::setEmailAddress);
    this.view.addPasswordValueListener(this::setPassword);
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

  public String getPassword() {
    return password;
  }

  private void setPassword(String password) {
    this.password = password;
  }
}
