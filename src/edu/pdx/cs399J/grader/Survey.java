package edu.pdx.cs410J.grader;

import org.apache.xerces.dom.*;
import org.apache.xml.serialize.*;
import org.w3c.dom.*;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * This program presents a survey that all students in CS410J should
 * answer.  It emails the results of the survey to the TA and emails a
 * receipt back to the student.
 *
 * <P align="center"><EM><A href =
 * "{@docRoot}/../src/edu/pdx/cs410J/grader/Survey.java">
 * View Source</A></EM></P>
 */
public class Survey {
  private static PrintWriter out = new PrintWriter(System.out, true);
  private static PrintWriter err = new PrintWriter(System.err, true);
  private static BufferedReader in = 
    new BufferedReader(new InputStreamReader(System.in));

  private static final String TA_EMAIL = "sjavata@cs.pdx.edu";
  private static String serverName = "mailhost.pdx.edu";

  /**
   * Returns a textual summary of a <code>Student</code>
   */
  private static String getSummary(Student student) {
    StringBuffer sb = new StringBuffer();
    sb.append("Name: " + student.getFullName() + "\n");
    sb.append("UNIX login: " + student.getId() + "\n");
    if(student.getEmail() != null) {
      sb.append("Email: " + student.getEmail() + "\n");
    }
    if(student.getSsn() != null) {
      sb.append("SSN: " + student.getSsn() + "\n");
    }
    if(student.getMajor() != null) {
      sb.append("Major: " + student.getMajor() + "\n");
    }
    return(sb.toString());
  }

  /**
   * Ask the student a question and return his response
   */
  private static String ask(String question) {
    out.print(question + " ");
    out.flush();

    String response = null;
    try {
      response = in.readLine();

    } catch(IOException ex) {
      err.println("** IOException while reading response: " + ex);
      System.exit(1);
    }

    return(response);
  }

  /**
   * Prints out usage information for this program
   */
  private static void usage() {
    err.println("\nusage: java Survey [options]");
    err.println("  where [options] are:");
    err.println("  -mailServer serverName    Mail server to send mail");
    err.println("\n");
    System.exit(1);
  }

  public static void main(String[] args) {
    // Parse the command line
    for(int i = 0; i < args.length; i++) {
      if(args[i].equals("-mailServer")) {
        if(++i >= args.length) {
          err.println("** Missing mail server name");
          usage();
        }

        serverName = args[i];

      } else if(args[i].startsWith("-")) {
        err.println("** Unknown command line option: " + args[i]);
        usage();

      } else {
        err.println("** Spurious command line: " + args[i]);
        usage();
      }
    }

    // Ask the student a bunch of questions
    out.println("\nWelcome to the CS410J Survey Program.  I'd like " +
                "to ask you a couple of");
    out.println("questions about yourself.  Except for your UNIX " +
                "login id, no question");
    out.println("is mandatory.  Your answers will be emailed to " +
                "the TA and a receipt");
    out.println("will be emailed to you.");
    out.println("");

    String firstName = ask("What is your first name?");
    String lastName = ask("What is your last name?");
    String nickName = ask("Is there a nick name that you go by?");
    String id = ask("MANDATORY: What is your UNIX login id?");

    if(id == null || id.equals("")) {
      err.println("** You must enter a valid UNIX login id");
      System.exit(1);
    }

    String email = ask("What is your email address (doesn't have " +
                       "to be PSU)?");
    String ssn = ask("What is your social security number (XXX-XX-XXXX)?");
    String major = ask("What is your major?");
    String learn = ask("What do you hope to learn in CS410J?");
    String comments = ask("Is there anything else you'd like to tell me?");

    // Create a Student instance based on the response
    Student student = new Student(id);

    if(firstName != null && !firstName.equals("")) {
      student.setFirstName(firstName);
    }

    if(lastName != null && !lastName.equals("")) {
      student.setLastName(lastName);
    }

    if(nickName != null && !nickName.equals("")) {
      student.setNickName(nickName);
    }

    if(email != null && !email.equals("")) {
      student.setEmail(email);
    }

    if(ssn != null && !ssn.equals("")) {
      student.setSsn(ssn);
    }

    if(major != null && !major.equals("")) {
      student.setMajor(major);
    }

    String summary = getSummary(student);

    out.println("\nYou entered the following information about " +
                "yourself:\n");
    out.println(summary);

    String verify = ask("\nIs this information correct (y/n)?");
    if(!verify.equals("y")) {
      err.println("** Not sending information.  Exiting.");
      System.exit(1);
    }

    // Create a temporary file to hold the Student's XML file
    File xmlFile = null;

    try {
      xmlFile = File.createTempFile("CS410J-Survey", ".xml");
      Document xmlDoc = XmlDumper.toXml(student);

      OutputFormat format = new OutputFormat(xmlDoc);
      format.setIndenting(true);
      format.setIndent(2);
      format.setLineWidth(70);
    
      PrintWriter pw = new PrintWriter(new FileWriter(xmlFile), true);
      XMLSerializer serial = new XMLSerializer(pw, format);
      serial.asDOMSerializer();
      serial.serialize(xmlDoc);  

    } catch(IOException ex) {
      err.println("** While saving XML file: " + ex);
      System.exit(1);
    }

    // Email the results of the survey to the TA and CC the student
    
    // Obtain a JavaMail session for sending email
    Properties props = new Properties();
    props.put("mail.smtp.host", serverName);
    Session session = Session.getDefaultInstance(props, null);

    // Make a new email message
    MimeMessage message = new MimeMessage(session);

    try {
      InternetAddress[] to = {new InternetAddress(TA_EMAIL)};
      message.setRecipients(Message.RecipientType.TO, to);

      String studentEmail = student.getEmail();
      if(studentEmail != null) {
        InternetAddress[] cc = {new InternetAddress(studentEmail)};
        message.setRecipients(Message.RecipientType.CC, cc);
      }

      message.setSubject("CS410J Survey for " + student.getFullName());

    } catch(AddressException ex) {
      err.println("** Exception with email address " + ex);
      System.exit(1);

    } catch(MessagingException ex) {
      err.println("** Exception while setting recipients email:" +
                  ex);
      System.exit(1);
    }

    // Create the text portion of the message
    StringBuffer text = new StringBuffer();
    text.append("Results of CS410J Survey:\n\n");
    text.append(summary);
    text.append("\n\nWhat do you hope to learn in CS410J?\n\n");
    text.append(learn);
    text.append("\n\nIs there anything else you'd like to tell me?\n\n");
    text.append(comments);
    text.append("\n\nThanks for filling out this survey!\n\nDave");

    MimeBodyPart textPart = new MimeBodyPart();
    try {
      textPart.setContent(text.toString(), "text/plain");

      // Try not to display text as separate attachment
      textPart.setDisposition("inline");

    } catch(MessagingException ex) {
      err.println("** Exception with text part: " + ex);
      System.exit(1);
    }
    
    // Attach the XML file
    DataSource ds = new FileDataSource(xmlFile);
    DataHandler dh = new DataHandler(ds);
    MimeBodyPart filePart = new MimeBodyPart();
    try {
      String xmlFileTitle = student.getId() + ".xml";

      filePart.setDataHandler(dh);
      filePart.setFileName(xmlFileTitle);
      filePart.setDescription("XML file for " + student.getFullName());

    } catch(MessagingException ex) {
      err.println("** Exception with file part: " + ex);
      System.exit(1);
    }

    // Finally, add the attachments to the message and send it
    try {
      Multipart mp = new MimeMultipart();
      mp.addBodyPart(textPart);
      mp.addBodyPart(filePart);

      message.setContent(mp);

      Transport.send(message);

    } catch(MessagingException ex) {
      err.println("** Exception while adding parts and sending: " +
		  ex);
      System.exit(1);
    }
  }

}
