package edu.pdx.cs410J.grader;

import com.google.common.annotations.VisibleForTesting;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {
  /**
   * The grader's email address
   */
  @VisibleForTesting
  static final String TA_EMAIL = "sjavata@gmail.com";
  /**
   * The name of the SMTP server that is used to send email
   */
  protected static String serverName = "mailhost.cs.pdx.edu";

  private static int emailServerPort = 25;

  protected static MimeMessage newEmailTo(Session session, String recipient, String subject) throws MessagingException {
    return newEmailTo(session, TA_EMAIL, recipient, subject);
  }

  protected static MimeMessage newEmailTo(Session session, String sender, String recipient, String subject) throws MessagingException {
    return newEmailTo(session, sender, recipient, subject, null);
  }

  protected static MimeMessage newEmailTo(Session session, String sender, String recipient, String subject, InternetAddress replyTo) throws MessagingException {
    return newEmailTo(session, sender, new InternetAddress(recipient), subject, replyTo);
  }

  protected static MimeMessage newEmailTo(Session session, String sender, InternetAddress recipient, String subject, InternetAddress replyTo) throws MessagingException {
    return newEmailTo(session, recipient, subject, replyTo, new InternetAddress(sender));
  }

  protected static MimeMessage newEmailTo(Session session, InternetAddress recipient, String subject, InternetAddress replyTo, InternetAddress sender) throws MessagingException {
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
