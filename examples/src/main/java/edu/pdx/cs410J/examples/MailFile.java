package edu.pdx.cs410J.examples;

import java.io.*;
import java.util.*;
import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * This program uses the JavaMail API to send a file to someone as a
 * MIME attachment to an email.  More information about the JavaMail
 * API is available from http://java.sun.com/products/javamail.
 *
 * @author David Whitlock
 */
public class MailFile {

  private static PrintWriter err = new PrintWriter(System.err, true);

  /**
   * Prints out information about how to use this program.
   */
  private static void usage() {
    err.println("usage: MailFile [options] file recipient [subject]");
    err.println("  Where [options] are:");
    err.println("  -server name   Name of the SMTP server");
    err.println("  -verbose       Print out extra info");
    System.exit(1);
  }

  /**
   * Reads the name of the file to be sent, the recipient of the
   * email, and the subject of the email (which may contain multiple
   * words) from the command line.  Then uses the JavaMail API to
   * construct an email message.
   */
  public static void main(String[] args) {
    StringBuffer subject = null;
    String fileName = null;
    String recipient = null;
    String serverName = "mailhost.pdx.edu";
    boolean debug = false;

    // Parse the command line
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-server")) {
	if(++i >= args.length) {
	  err.println("** Missing server name");
	  usage();
	}

	serverName = args[i];

      } if (args[i].equals("-verbose")) {
	debug = true;

      } else if (fileName == null) {
	fileName = args[i];

      } else if (recipient == null) {
	recipient = args[i];

      } else {
	// Part of the subject
	if(subject == null) {
	  subject = new StringBuffer();
	}

	subject.append(args[i] + " ");
      }
    }

    // Check to make sure everything is okay
    if (fileName == null) {
      err.println("** No file specified");
      usage();
    }

    if (recipient == null) {
      err.println("** No recipient specified");
      usage();
    }

    if (subject == null) {
      // Default subject
      subject = new StringBuffer("A file for you");
    }

    // Obtain a Session for sending email
    Properties props = new Properties();
    props.put("mail.smtp.host", serverName);
    Session session = Session.getDefaultInstance(props, null);
    session.setDebug(debug);

    MimeMessage message = new MimeMessage(session);

    try {
      InternetAddress[] to = {new InternetAddress(recipient)};
      message.setRecipients(Message.RecipientType.TO, to);

    } catch (AddressException ex) {
      err.println("** Invalid address: " + recipient);
      System.exit(1);

    } catch (MessagingException ex) {
      err.println("** MessagingException: " + ex);
      System.exit(1);
    }
    
    File file = new File(fileName);
    if (!file.exists()) {
      err.println("** File " + file + " does not exist!");
      System.exit(1);
    }

    // This will be a multi-part MIME message
    MimeBodyPart textPart = new MimeBodyPart();
    try {
      textPart.setContent("File " + file, "text/plain");

    } catch (MessagingException ex) {
      err.println("** Exception with text part: " + ex);
      System.exit(1);
    }

    // Read in the file and make a MimeBodyPart out of it
    DataSource ds = new FileDataSource(file);
    DataHandler dh = new DataHandler(ds);
    MimeBodyPart filePart = new MimeBodyPart();
    try {
      filePart.setDataHandler(dh);
      filePart.setFileName(file.getName());
      filePart.setDescription("The file you requested");

    } catch (MessagingException ex) {
      err.println("** Exception with file part: " + ex);
      System.exit(1);
    }

    try {
      Multipart mp = new MimeMultipart();
      mp.addBodyPart(textPart);
      mp.addBodyPart(filePart);

      message.setContent(mp);

      // Finally, send the message
      Transport.send(message);

    } catch (MessagingException ex) {
      err.println("** Exception while adding parts and sending: " +
		  ex);
      System.exit(1);
    }
    
  }
  

}
