package edu.pdx.cs410J.grader;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {
  /**
   * The name of the SMTP server that is used to send email
   */
  protected String serverName = "mailhost.cs.pdx.edu";

  protected MimeMessage newEmailTo(Session session, String recipient, String subject) throws MessagingException {
    // Make a new email message
    MimeMessage message = new MimeMessage(session);

    InternetAddress[] to = {new InternetAddress(recipient)};
    message.setRecipients(Message.RecipientType.TO, to);
    message.setSubject(subject);
    return message;
  }

  protected Session newEmailSession(boolean debug) {
    // Obtain a Session for sending email
    Properties props = new Properties();
    props.put("mail.smtp.host", serverName);
    Session session = Session.getDefaultInstance(props, null);
    session.setDebug(debug);
    return session;
  }
}
