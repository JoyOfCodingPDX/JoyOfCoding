package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.EmailAttachmentProcessor;
import edu.pdx.cs410J.grader.GraderEmailAccount;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

@Singleton
public class POASubmissionsDownloader {

  private final EventBus bus;

  @Inject
  public POASubmissionsDownloader(EventBus bus) {
    this.bus = bus;
    this.bus.register(this);
  }

  @Subscribe
  public void downloadSubmissions(EmailCredentials credentials) throws MessagingException {
    GraderEmailAccount account = new GraderEmailAccount(credentials.getEmailAddress(), credentials.getPassword());
    account.fetchAttachmentsFromUnreadMessagesInFolder("poa", new POAAttachmentProcessor());
  }

  private void extractPOASubmissionFromAttachment(Message message, String fileName, InputStream inputStream) {
    try {
      String submitter = getSender(message);
      LocalDateTime submitTime = getTimeMessageWasSent(message);
      String content = extractPOAContentFrom(inputStream);
      POASubmission submission = POASubmission.builder()
        .setSubject(message.getSubject())
        .setSubmitter(submitter)
        .setSubmitTime(submitTime)
        .setContent(content)
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
    public void processAttachment(Message message, String fileName, InputStream inputStream) {
      extractPOASubmissionFromAttachment(message, fileName, inputStream);
    }

    @Override
    public Iterable<? extends String> getSupportedContentTypes() {
      return Arrays.asList("text/plain", "text/html");
    }
  }
}
