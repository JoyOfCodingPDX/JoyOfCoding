package edu.pdx.cs.joy.grader.poa;

import com.google.common.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

public class EmailCredentialsPresenterTest extends EventBusTestCase {

  private EmailCredentialsView view;
  private EmailCredentialsPresenter presenter;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();

    this.view = mock(EmailCredentialsView.class);

    presenter = new EmailCredentialsPresenter(bus, view);
  }

  @Test
  public void downloadingSubmissionsDisplaysEmailCredentialsView() {
    this.bus.post(new DownloadPOASubmissionsRequest());

    verify(this.view).setIsVisible(true);
  }

  @Test
  public void downloadingSubmissionsDisplaysStatusMessage() {
    StatusMessageListener listener = mock(StatusMessageListener.class);
    this.bus.register(listener);

    this.bus.post(new DownloadPOASubmissionsRequest());

    ArgumentCaptor<StatusMessage> captor = ArgumentCaptor.forClass(StatusMessage.class);
    verify(listener).handleStatusMessage(captor.capture());

    StatusMessage status = captor.getValue();
    assertThat(status.getStatusMessage(), equalTo(EmailCredentialsPresenter.ENTER_CREDENTIALS_MESSAGE));
  }

  @Test
  void downloadingSubmissionsWithNoCredentialsDoesNotPopulateView() {
    this.bus.post(new DownloadPOASubmissionsRequest());

    verify(this.view, never()).setEmailAddress(any(String.class));
    verify(this.view, never()).setPassword(any(String.class));
  }

  @Test
  void downloadingSubmissionsWithCredentialsPopulatesView() {
    String email = "me@me.com";
    String password = "password";
    this.bus.post(new DownloadPOASubmissionsRequest(email, password));

    verify(this.view).setEmailAddress(eq(email));
    verify(this.view).setPassword(eq(password));
  }

  @Test
  public void emailAddressInViewIsSavedToPresenter() {
    ArgumentCaptor<EmailCredentialsView.EmailAddressValueListener> emailAddress = ArgumentCaptor.forClass(EmailCredentialsView.EmailAddressValueListener.class);
    verify(this.view).addEmailAddressValueListener(emailAddress.capture());

    String address = "me@email.com";
    emailAddress.getValue().setEmailAddress(address);

    assertThat(presenter.getEmailAddress(), equalTo(address));
  }

  @Test
  public void passwordInViewIsSavedToPresenter() {
    ArgumentCaptor<EmailCredentialsView.PasswordValueListener> passwordCaptor = ArgumentCaptor.forClass(EmailCredentialsView.PasswordValueListener.class);
    verify(this.view).addPasswordValueListener(passwordCaptor.capture());

    String password = "password";
    passwordCaptor.getValue().setPassword(password);

    assertThat(presenter.getPassword(), equalTo(password));
  }

  @Test
  public void enteringCredentialsInViewFiresEmailCredentialsEvent() {
    ArgumentCaptor<EmailCredentialsView.SubmitCredentialsListener> submitListener = ArgumentCaptor.forClass(EmailCredentialsView.SubmitCredentialsListener.class);
    verify(this.view).addSubmitCredentialsListener(submitListener.capture());

    EmailCredentialsListener listener = mock(EmailCredentialsListener.class);
    this.bus.register(listener);

    String emailAddress = "me@email.com";
    this.presenter.setEmailAddress(emailAddress);
    String password = "password";
    this.presenter.setPassword(password);

    submitListener.getValue().submitCredentials();

    ArgumentCaptor<EmailCredentials> credentials = ArgumentCaptor.forClass(EmailCredentials.class);
    verify(listener).emailCredentials(credentials.capture());

    assertThat(credentials.getValue().getEmailAddress(), equalTo(emailAddress));
    assertThat(credentials.getValue().getPassword(), equalTo(password));
  }

  public interface EmailCredentialsListener {
    @Subscribe
    void emailCredentials(EmailCredentials event);
  }

  private interface StatusMessageListener {
    @Subscribe
    void handleStatusMessage(StatusMessage status);
  }
}
