package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.Subscribe;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static edu.pdx.cs410J.grader.poa.EmailCredentialsView.EmailAddressValueListener;
import static edu.pdx.cs410J.grader.poa.EmailCredentialsView.PasswordValueListener;
import static edu.pdx.cs410J.grader.poa.EmailCredentialsView.SubmitCredentialsListener;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EmailCredentialsPresenterTest extends EventBusTestCase {

  private EmailCredentialsView view;
  private EmailCredentialsPresenter presenter;

  @Override
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
  public void emailAddressInViewIsSavedToPresenter() {
    ArgumentCaptor<EmailAddressValueListener> emailAddress = ArgumentCaptor.forClass(EmailAddressValueListener.class);
    verify(this.view).addEmailAddressValueListener(emailAddress.capture());

    String address = "me@email.com";
    emailAddress.getValue().setEmailAddress(address);

    assertThat(presenter.getEmailAddress(), equalTo(address));
  }

  @Test
  public void passwordInViewIsSavedToPresenter() {
    ArgumentCaptor<PasswordValueListener> passwordCaptor = ArgumentCaptor.forClass(PasswordValueListener.class);
    verify(this.view).addPasswordValueListener(passwordCaptor.capture());

    String password = "password";
    passwordCaptor.getValue().setPassword(password);

    assertThat(presenter.getPassword(), equalTo(password));
  }

  @Test
  public void enteringCredentialsInViewFiresEmailCredentialsEvent() {
    ArgumentCaptor<SubmitCredentialsListener> submitListener = ArgumentCaptor.forClass(SubmitCredentialsListener.class);
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

}
