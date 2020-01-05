package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.icegreen.greenmail.imap.AuthorizationException;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.user.GreenMailUser;
import edu.pdx.cs410J.grader.GreenmailIntegrationTestCase;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class POASubmissionsDownloaderIT extends GreenmailIntegrationTestCase  {

  @Test
  public void multipartMessageWithTextPart() throws MessagingException {
    String subject = "Email subject";
    String poa = "This is my POA";
    String sender = "sender@email.com";
    mailMultiPartPOA(sender, subject, poa);

    assertEmailIsProperlyProcessed(subject, poa, sender);
  }

  @Test
  public void textPlainMessage() throws MessagingException {
    String subject = "Email subject";
    String poa = "This is my POA";
    String sender = "sender@email.com";
    mailTextPlainPOA(sender, subject, poa);

    assertEmailIsProperlyProcessed(subject, poa, sender);
  }

  private void mailTextPlainPOA(String sender, String subject, String poa) throws MessagingException {
    MimeMessage message = newEmailTo(newEmailSession(true), emailAddress, subject);
    message.setFrom(sender);

    message.setContent(poa, "text/plain");

    Transport.send(message);
  }

  private void assertEmailIsProperlyProcessed(String subject, String poa, String sender) {
    EventBus bus = new EventBusThatPublishesUnhandledExceptionEvents();
    POASubmissionHandler handler = mock(POASubmissionHandler.class);
    bus.register(handler);

    POASubmissionsDownloader downloader = new POASubmissionsDownloader(bus);
    EmailCredentials credentials = new EmailCredentials(this.imapUserName, this.imapPassword);
    downloader.downloadSubmissions(this.emailServerHost, this.imapsPort, credentials);

    ArgumentCaptor<POASubmission> captor = ArgumentCaptor.forClass(POASubmission.class);
    verify(handler).handlePOASubmission(captor.capture());

    POASubmission submission = captor.getValue();
    assertThat(submission.getSubject(), equalTo(subject));
    assertThat(submission.getContent(), equalTo(poa));
    assertThat(submission.getSubmitter(), equalTo(sender));
  }

  private void mailMultiPartPOA(String sender, String emailSubject, String poa) throws MessagingException {
    MimeMessage message = newEmailTo(newEmailSession(true), emailAddress, emailSubject);
    message.setFrom(sender);

    MimeBodyPart textPart = new MimeBodyPart();
    textPart.setContent(poa, "text/plain");
    textPart.setDisposition("inline");

    Multipart mp = new MimeMultipart();
    mp.addBodyPart(textPart);

    message.setContent(mp);

    Transport.send(message);
  }

  @Override
  protected void doSomethingWithUser(GreenMailUser user) throws FolderException, AuthorizationException {
    super.doSomethingWithUser(user);

    moveEmailsFromInboxToFolder(user, POASubmissionsDownloader.POA_FOLDER_NAME);
  }

  private interface POASubmissionHandler {
    @Subscribe
    void handlePOASubmission(POASubmission submission);
  }
}