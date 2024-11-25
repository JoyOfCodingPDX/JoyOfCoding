package edu.pdx.cs.joy.grader.poa;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class EmailCredentialsPresenter {
  @VisibleForTesting
  static final String ENTER_CREDENTIALS_MESSAGE = "Enter email address and password";

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
    if (this.emailAddress != null && this.password != null) {
      this.bus.post(new EmailCredentials(this.emailAddress, this.password));
    }
  }

  @Subscribe
  public void displayEmailCredentialsView(DownloadPOASubmissionsRequest event) {
    if (event.getEmailAddress() != null) {
      this.view.setEmailAddress(event.getEmailAddress());
    }
    if (event.getPassword() != null) {
      this.view.setPassword(event.getPassword());
    }
    fireStatusMessageEvent(ENTER_CREDENTIALS_MESSAGE);
    this.view.setIsVisible(true);
  }

  private void fireStatusMessageEvent(String message) {
    this.bus.post(new StatusMessage(message));
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
