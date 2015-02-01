package edu.pdx.cs410J.grader.poa;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static edu.pdx.cs410J.grader.poa.EmailCredentialsView.EmailAddressValueListener;
import static edu.pdx.cs410J.grader.poa.EmailCredentialsView.PasswordValueListener;
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
    this.bus.post(new DownloadSubmissions());

    verify(this.view).setVisible(true);
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
}
