package edu.pdx.cs410J.grader.poa;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EmailCredentialsPresenterTest extends EventBusTestCase {

  private EmailCredentialsView view;

  @Override
  public void setUp() {
    super.setUp();

    this.view = mock(EmailCredentialsView.class);

    new EmailCredentialsPresenter(bus, view);
  }

  @Test
  public void downloadingSubmissionsDisplaysEmailCredentialsView() {
    this.bus.post(new DownloadSubmissions());

    verify(this.view).setVisible(true);
  }
}
