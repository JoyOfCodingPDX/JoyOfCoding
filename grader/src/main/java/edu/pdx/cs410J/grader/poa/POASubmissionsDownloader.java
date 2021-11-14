package edu.pdx.cs410J.grader.poa;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import edu.pdx.cs410J.grader.EmailAttachmentProcessor;
import edu.pdx.cs410J.grader.GraderEmailAccount;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

@Singleton
public class POASubmissionsDownloader {

  static final String POA_FOLDER_NAME = "poa";
  private final EventBus bus;
  private final Executor executor;

  @Inject
  public POASubmissionsDownloader(EventBus bus, @Named("POADownloaderExecutor") Executor executor) {
    this.bus = bus;
    this.executor = executor;
    this.bus.register(this);
  }

  @Subscribe
  public void downloadSubmissions(EmailCredentials credentials) throws MessagingException {
    GraderEmailAccount account = new GraderEmailAccount(credentials.getEmailAddress(), credentials.getPassword(), this::fireStatusMessage);
    downloadSubmissions(account);
  }

  @VisibleForTesting
  Future<?> downloadSubmissions(String emailServerHostName, int emailServerHostPort, EmailCredentials credentials) {
      GraderEmailAccount account = new GraderEmailAccount(emailServerHostName, emailServerHostPort, credentials.getEmailAddress(), credentials.getPassword(), true, this::fireStatusMessage);
      return downloadSubmissions(account);
    }

  private Future<?> downloadSubmissions(GraderEmailAccount account) {
    FutureTask<Void> future = new FutureTask<>(() -> account.fetchAttachmentsFromUnreadMessagesInFolder(POA_FOLDER_NAME, new POAAttachmentProcessor()), null);
    this.executor.execute(future);
    return future;
  }

  private void fireStatusMessage(String statusMessage) {
    this.bus.post(new StatusMessage(statusMessage));
  }

  private void extractPOASubmissionFromAttachment(Message message, InputStream inputStream, String contentType) {
    try {
      String submitter = getSender(message);
      LocalDateTime submitTime = getTimeMessageWasSent(message);
      String content = extractPOAContentFrom(inputStream);
      POASubmission submission = POASubmission.builder()
        .setSubject(message.getSubject())
        .setSubmitter(submitter)
        .setSubmitTime(submitTime)
        .setContent(content)
        .setContentType(contentType)
        .create();

      this.bus.post(submission);

    } catch (IOException | MessagingException ex) {
      throw new IllegalStateException("While working with message", ex);
    }

  }

  private String extractPOAContentFrom(InputStream inputStream) throws IOException {
    StringWriter sw = new StringWriter();
    CharStreams.copy(new InputStreamReader(inputStream), sw);
    return sw.toString();
  }

  private LocalDateTime getTimeMessageWasSent(Message message) throws MessagingException {
    Date date = message.getSentDate();
    return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
  }

  private String getSender(Message message) throws MessagingException {
    return message.getFrom()[0].toString();
  }

  private class POAAttachmentProcessor implements EmailAttachmentProcessor {
    @Override
    public void processAttachment(Message message, String fileName, InputStream inputStream, String contentType) {
      extractPOASubmissionFromAttachment(message, inputStream, contentType);
    }

    @Override
    public Iterable<? extends String> getSupportedContentTypes() {
      return Arrays.asList("text/plain", "text/html");
    }
  }
}
