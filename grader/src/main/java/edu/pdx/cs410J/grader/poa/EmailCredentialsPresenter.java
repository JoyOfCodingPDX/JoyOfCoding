package edu.pdx.cs410J.grader.poa;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.mvp.PresenterOnEventBus;

@Singleton
public class EmailCredentialsPresenter extends PresenterOnEventBus {
  private final EmailCredentialsView view;
  private String emailAddress;
  private String password;

  @Inject
  public EmailCredentialsPresenter(EventBus bus, EmailCredentialsView view) {
    super(bus);
    this.view = view;

    this.view.addEmailAddressValueListener(this::setEmailAddress);
    this.view.addPasswordValueListener(this::setPassword);
    this.view.addSubmitCredentialsListener(this::fireEmailCredentialsEvent);
  }

  private void fireEmailCredentialsEvent() {
    publishEvent(new EmailCredentials(this.emailAddress, this.password));
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
