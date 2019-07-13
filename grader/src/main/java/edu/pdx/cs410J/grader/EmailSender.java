package edu.pdx.cs410J.grader;

import com.google.common.annotations.VisibleForTesting;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {
  /**
   * The grader's email address
   */
  @VisibleForTesting
  static final InternetAddress TA_EMAIL = getGraderEmailAddress();

  private static InternetAddress getGraderEmailAddress() {
    try {
      return new InternetAddress("sjavata@gmail.com");

    } catch (AddressException ex) {
      throw new IllegalStateException("Could not create Grader's email address", ex);
    }
  }

  /**
   * The name of the SMTP server that is used to send email
   */
  protected static String serverName = "mailhost.cs.pdx.edu";

  private static int emailServerPort = 25;

  protected static NewEmail newEmailTo(Session session, InternetAddress recipient) {
    return new NewEmail(session).to(recipient);
  }

  protected static class NewEmail {
    private final Session session;
    private InternetAddress recipient;
    private InternetAddress sender;
    private InternetAddress replyTo;
    private String subject;

    private NewEmail(Session session) {
      this.session = session;
    }

    MimeMessage createMessage() throws MessagingException {

    // Make a new email message
    MimeMessage message = new MimeMessage(session);

    message.setRecipients(Message.RecipientType.TO, new InternetAddress[] { recipient });
    message.setFrom(sender);
    message.setSubject(subject);
    if (replyTo != null) {
      message.setReplyTo(new InternetAddress[] { replyTo });
    }
    return message;
    }

    private NewEmail to(InternetAddress recipient) {
      this.recipient = recipient;
      return this;
    }

    public NewEmail from(InternetAddress sender) {
      this.sender = sender;
      return this;
    }

    public NewEmail withSubject(String subject) {
      this.subject = subject;
      return this;
    }

    public NewEmail replyTo(InternetAddress replyTo) {
      this.replyTo = replyTo;
      return this;
    }
  }

  protected static Session newEmailSession(boolean debug) {
    // Obtain a Session for sending email
    Properties props = new Properties();
    props.put("mail.smtp.host", serverName);
    props.put("mail.smtp.port", emailServerPort);
    props.put("mail.smtp.localhost", "127.0.0.1");
    Session session = Session.getDefaultInstance(props, null);
    session.setDebug(debug);
    return session;
  }

  public void setEmailServerPort(int port) {
    emailServerPort = port;
  }
}
