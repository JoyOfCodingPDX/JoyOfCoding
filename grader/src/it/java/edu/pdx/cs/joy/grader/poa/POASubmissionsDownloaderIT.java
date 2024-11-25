package edu.pdx.cs.joy.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.icegreen.greenmail.imap.AuthorizationException;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.user.GreenMailUser;
import edu.pdx.cs.joy.grader.GreenmailIntegrationTestCase;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class POASubmissionsDownloaderIT extends GreenmailIntegrationTestCase  {

  @Test
  public void multipartMessageWithTextPart() throws MessagingException, ExecutionException, InterruptedException {
    String subject = "Email subject";
    String poa = "This is my POA";
    String sender = "sender@email.com";
    String contentType = "TEXT/PLAIN; charset=us-ascii";
    mailMultiPartPOA(sender, subject, poa);

    assertEmailIsProperlyProcessed(subject, poa, sender, contentType);
  }

  @Test
  public void textPlainMessage() throws MessagingException, ExecutionException, InterruptedException {
    String subject = "Email subject";
    String poa = "This is my POA";
    String sender = "sender@email.com";
    String contentType = "TEXT/PLAIN; charset=us-ascii";
    mailSinglePartPOA(sender, subject, poa, contentType);

    assertEmailIsProperlyProcessed(subject, poa, sender, contentType);
  }

  private void mailSinglePartPOA(String sender, String subject, String poa, String mimeType) throws MessagingException {
    MimeMessage message = newEmailTo(newEmailSession(true), emailAddress, subject);
    message.setFrom(sender);

    message.setContent(poa, mimeType);

    Transport.send(message);
  }

  @Test
  public void textHTMLMessage() throws MessagingException, ExecutionException, InterruptedException {
    String subject = "Email subject";
    String poa = "<html><div>This is my HTML POA</div></html>";
    String sender = "sender@email.com";
    String contentType = "TEXT/HTML; charset=us-ascii";
    mailSinglePartPOA(sender, subject, poa, contentType);

    assertEmailIsProperlyProcessed(subject, poa, sender, contentType);
  }

  private void assertEmailIsProperlyProcessed(String subject, String poa, String sender, String contentType) throws ExecutionException, InterruptedException {
    EventBus bus = new EventBusThatPublishesUnhandledExceptionEvents();
    POASubmissionHandler handler = mock(POASubmissionHandler.class);
    bus.register(handler);

    StatusMessageHandler statusHandler = mock(StatusMessageHandler.class);
    bus.register(statusHandler);

    POASubmissionsDownloader downloader = new POASubmissionsDownloader(bus, Executors.newSingleThreadExecutor());
    EmailCredentials credentials = new EmailCredentials(this.imapUserName, this.imapPassword);
    Future<?> future = downloader.downloadSubmissions(this.emailServerHost, this.imapsPort, credentials);
    assertThat(future.get(), nullValue());

    ArgumentCaptor<POASubmission> captor = ArgumentCaptor.forClass(POASubmission.class);
    verify(handler).handlePOASubmission(captor.capture());

    POASubmission submission = captor.getValue();
    assertThat(submission.getSubject(), equalTo(subject));
    assertThat(submission.getContent(), equalTo(poa));
    assertThat(submission.getContentType(), equalTo(contentType));
    assertThat(submission.getSubmitter(), equalTo(sender));

    ArgumentCaptor<StatusMessage> statusCaptor = ArgumentCaptor.forClass(StatusMessage.class);
    verify(statusHandler, atLeastOnce()).handleStatusMessage(statusCaptor.capture());

    List<StatusMessage> statuses = statusCaptor.getAllValues();
    assertThat(statuses.get(0).getStatusMessage(), equalTo("Getting messages from \"poa\" folder"));
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

  private interface StatusMessageHandler {
    @Subscribe
    void handleStatusMessage(StatusMessage message);
  }
}