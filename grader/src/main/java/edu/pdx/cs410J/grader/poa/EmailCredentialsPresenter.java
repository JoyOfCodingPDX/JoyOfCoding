package edu.pdx.cs410J.grader.poa;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class EmailCredentialsPresenter {
  private final EventBus bus;
  private final EmailCredentialsView view;
  private String emailAddress;
  private String password;

  @Inject
  public EmailCredentialsPresenter(EventBus bus, EmailCredentialsView view) {
    this.bus = bus;
    this.view = view;

    this.bus.register(this);

    this.view.addEmailAddressValueListener(this::setEmailAddress);
    this.view.addPasswordValueListener(this::setPassword);
    this.view.addSubmitCredentialsListener(this::fireEmailCredentialsEvent);
  }

  private void fireEmailCredentialsEvent() {
    this.bus.post(new EmailCredentials(this.emailAddress, this.password));
  }

  @Subscribe
  public void displayEmailCredentialsView(DownloadPOASubmissionsRequest event) {
    this.view.setIsVisible(true);
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  @VisibleForTesting
  void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public String getPassword() {
    return password;
  }

  @VisibleForTesting
  void setPassword(String password) {
    this.password = password;
  }
}
