package edu.pdx.cs410J.grader;

import com.google.common.annotations.VisibleForTesting;
import org.w3c.dom.Document;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.function.Consumer;

/**
 * This program presents a survey that all students in CS410J should
 * answer.  It emails the results of the survey to the TA and emails a
 * receipt back to the student.
 */
public class Survey extends EmailSender {
  private static final PrintWriter out = new PrintWriter(System.out, true);
  private static final PrintWriter err = new PrintWriter(System.err, true);
  private static final BufferedReader in =
    new BufferedReader(new InputStreamReader(System.in));

  private static final String TA_EMAIL = "sjavata@gmail.com";

  private static boolean saveStudentXmlFile = false;

  /**
   * Returns a textual summary of a <code>Student</code>
   */
  private static String getSummary(Student student) {
    StringBuilder sb = new StringBuilder();
    sb.append("Name: ").append(student.getFullName()).append("\n");
    sb.append("UNIX login: ").append(student.getId()).append("\n");
    if (student.getEmail() != null) {
      sb.append("Email: ").append(student.getEmail()).append("\n");
    }
    if (student.getSsn() != null) {
      sb.append("Student id: ").append(student.getSsn()).append("\n");
    }
    if (student.getMajor() != null) {
      sb.append("Major: ").append(student.getMajor()).append("\n");
    }
    sb.append("Enrolled in: ").append(student.getEnrolledSection().asString()).append("\n");
    return sb.toString();
  }

  /**
   * Ask the student a question and return his response
   */
  private static String ask(String question) {
    out.print(breakUpInto80CharacterLines(question));
    out.print(" ");
    out.flush();

    String response = null;
    try {
      response = in.readLine();

    } catch (IOException ex) {
      printErrorMessageAndExit("** IOException while reading response", ex);
    }

    return response;
  }

  /**
   * Prints out usage information for this program
   */
  private static void usage() {
    err.println("\nusage: java Survey [options]");
    err.println("  where [options] are:");
    err.println("  -mailServer serverName    Mail server to send mail");
    err.println("  -saveStudentXmlFile       Save a copy of the generated student.xml file");
    err.println("\n");
    System.exit(1);
  }

  public static void main(String[] args) {
    parseCommandLine(args);

    printIntroduction();

    Student student = gatherStudentInformation();

    String learn = ask("What do you hope to learn in CS410J?");
    String comments = ask("What else would you like to tell me?");

    addNotesToStudent(student, learn, comments);

    emailSurveyResults(student, learn, comments);
  }

  private static void addNotesToStudent(Student student, String learn, String comments) {
    if (isNotEmpty(learn)) {
      student.addNote(student.getFullName() + " would like to learn " + learn);
    }

    if (isNotEmpty(comments)) {
      student.addNote(student.getFullName() + " has these comments: " + comments);
    }
  }

  private static Student gatherStudentInformation() {
    String firstName = ask("What is your first name?");
    String lastName = ask("What is your last name?");
    String nickName = ask("What is your nickname? (Leave blank if " +
                          "you don't have one)");
    String id = ask("MANDATORY: What is your UNIX login id?");

    if (isEmpty(id)) {
      printErrorMessageAndExit("** You must enter a valid UNIX login id");
    }

    Student student = new Student(id);
    setValueIfNotEmpty(firstName, student::setFirstName);
    setValueIfNotEmpty(lastName, student::setLastName);
    setValueIfNotEmpty(nickName, student::setNickName);

    askQuestionAndSetValue("What is your email address (doesn't have to be PSU)?", student::setEmail);
    askQuestionAndSetValue("What is your student id (XXXXXXXXX)?", student::setSsn);
    askQuestionAndSetValue("What is your major?", student::setMajor);

    askEnrolledSectionQuestion(student);

    return student;
  }

  private static void askEnrolledSectionQuestion(Student student) {
    String answer = ask("MANDATORY: Are you enrolled in the undergraduate or graduate section of this course? [u/g]");
    if (isEmpty(answer)) {
      printErrorMessageAndExit("Missing enrolled section. Please enter a \"u\" or \"g\"");
    }

    if (answer.toLowerCase().startsWith("u")) {
      student.setEnrolledSection(Student.Section.UNDERGRADUATE);

    } else if (answer.toLowerCase().startsWith("g")) {
      student.setEnrolledSection(Student.Section.GRADUATE);

    } else {
      printErrorMessageAndExit("Unknown section \"" + answer + "\".  Please enter a \"u\" or \"g\"");
    }

  }

  private static void askQuestionAndSetValue(String question, Consumer<String> setter) {
    String answer = ask(question);
    setValueIfNotEmpty(answer, setter);
  }

  static void setValueIfNotEmpty(String string, Consumer<String> setter) {
    if (isNotEmpty(string)) {
      setter.accept(string);
    }
  }

  private static void emailSurveyResults(Student student, String learn, String comments) {
    String summary = verifyInformation(student);

    // Email the results of the survey to the TA and CC the student

    MimeMessage message = createEmailMessage(student);
    MimeBodyPart textPart = createEmailText(learn, comments, summary);
    MimeBodyPart xmlFilePart = createXmlAttachment(student);
    addAttachmentsAndSendEmail(message, textPart, xmlFilePart);
  }

  private static void addAttachmentsAndSendEmail(MimeMessage message, MimeBodyPart textPart, MimeBodyPart filePart) {
    // Finally, add the attachments to the message and send it
    try {
      Multipart mp = new MimeMultipart();
      mp.addBodyPart(textPart);
      mp.addBodyPart(filePart);

      message.setContent(mp);

      Transport.send(message);

      logSentEmail(message);

    } catch (MessagingException ex) {
      printErrorMessageAndExit("** Exception while adding parts and sending", ex);
    }
  }

  private static void logSentEmail(MimeMessage message) throws MessagingException {
    StringBuilder sb = new StringBuilder();
    sb.append("\nAn email with with subject \"");
    sb.append(message.getSubject());
    sb.append("\" was sent to ");

    Address[] recipients = message.getAllRecipients();
    for (int i = 0; i < recipients.length; i++) {
      Address recipient = recipients[i];
      sb.append(recipient);
      if (i < recipients.length - 2) {
        sb.append(", ");

      } else if (i < recipients.length - 1) {
        sb.append(" and ");
      }
    }

    out.println(breakUpInto80CharacterLines(sb.toString()));
  }

  private static MimeBodyPart createXmlAttachment(Student student) {
    byte[] xmlBytes = getXmlBytes(student);

    if (saveStudentXmlFile) {
      writeStudentXmlToFile(xmlBytes, student);
    }

    DataSource ds = new ByteArrayDataSource(xmlBytes, "text/xml");
    DataHandler dh = new DataHandler(ds);
    MimeBodyPart filePart = new MimeBodyPart();
    try {
      String xmlFileTitle = student.getId() + ".xml";

      filePart.setDataHandler(dh);
      filePart.setFileName(xmlFileTitle);
      filePart.setDescription("XML file for " + student.getFullName());

    } catch (MessagingException ex) {
      printErrorMessageAndExit("** Exception with file part", ex);
    }
    return filePart;
  }

  private static void writeStudentXmlToFile(byte[] xmlBytes, Student student) {
    File directory = new File(System.getProperty("user.dir"));
    File file = new File(directory, student.getId() + ".xml");
    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(xmlBytes);
      fos.flush();

    } catch (IOException e) {
      printErrorMessageAndExit("Could not write student XML file: " + file, e);
    }

    out.println("\nSaved student XML file to " + file + "\n");
  }

  private static MimeBodyPart createEmailText(String learn, String comments, String summary) {
    // Create the text portion of the message
    StringBuilder text = new StringBuilder();
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

    } catch (MessagingException ex) {
      printErrorMessageAndExit("** Exception with text part", ex);
    }
    return textPart;
  }

  private static MimeMessage createEmailMessage(Student student) {
    MimeMessage message = null;
    try {
      message = newEmailTo(newEmailSession(false), TA_EMAIL, "CS410J Survey for " + student.getFullName());

      String studentEmail = student.getEmail();
      if (studentEmail != null) {
        InternetAddress[] cc = {new InternetAddress(studentEmail)};
        message.setRecipients(Message.RecipientType.CC, cc);
      }

    } catch (AddressException ex) {
      printErrorMessageAndExit("** Exception with email address", ex);

    } catch (MessagingException ex) {
      printErrorMessageAndExit("** Exception while setting recipients email", ex);
    }
    return message;
  }

  private static byte[] getXmlBytes(Student student) {
    // Create a temporary "file" to hold the Student's XML file.  We
    // use a byte array so that potentially sensitive data (SSN, etc.)
    // is not written to disk
    byte[] bytes = null;

    Document xmlDoc = XmlDumper.toXml(student);


    try {
      bytes = XmlHelper.getBytesForXmlDocument(xmlDoc);

    } catch (TransformerException ex) {
      ex.printStackTrace(System.err);
      System.exit(1);
    }
    return bytes;
  }

  private static String verifyInformation(Student student) {
    String summary = getSummary(student);

    out.println("\nYou entered the following information about " +
                "yourself:\n");
    out.println(summary);

    String verify = ask("\nIs this information correct (y/n)?");
    if (!verify.equals("y")) {
      printErrorMessageAndExit("** Not sending information.  Exiting.");
    }
    return summary;
  }

  private static void printErrorMessageAndExit(String message) {
    printErrorMessageAndExit(message, null);
  }

  private static void printErrorMessageAndExit(String message, Throwable ex) {
    err.println(message);
    if (ex != null) {
      ex.printStackTrace(err);
    }
    System.exit(1);
  }

  private static boolean isNotEmpty(String string) {
    return string != null && !string.equals("");
  }

  private static boolean isEmpty(String string) {
    return string == null || string.equals("");
  }

  private static void printIntroduction() {
    // Ask the student a bunch of questions
    String welcome =
      "Welcome to the CS410J Survey Program.  I'd like to ask you a couple of " +
      "questions about yourself.  Except for your UNIX login id and the section " +
      "that you are enrolled in, no question" +
      "is mandatory.  Your answers will be emailed to the TA and a receipt " +
      "will be emailed to you.";

    out.println("");
    out.println(breakUpInto80CharacterLines(welcome));
    out.println("");
  }

  @VisibleForTesting
  static String breakUpInto80CharacterLines(String message) {
    StringBuilder sb = new StringBuilder();
    int currentLineLength = 0;
    String[] words = message.split(" ");
    for (String word : words) {
      if (currentLineLength + word.length() > 80) {
        sb.append('\n');
        sb.append(word);
        currentLineLength = word.length();

      } else {
        if (currentLineLength > 0) {
          sb.append(' ');
        }
        sb.append(word);
        currentLineLength += word.length() + 1;
      }

    }
    return sb.toString();
  }

  private static void parseCommandLine(String[] args) {
    // Parse the command line
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if (arg.equals("-mailServer")) {
        if (++i >= args.length) {
          err.println("** Missing mail server name");
          usage();
        }

        serverName = arg;

      } else if (arg.equals("-saveStudentXmlFile")) {
        saveStudentXmlFile = true;

      } else if (arg.startsWith("-")) {
        err.println("** Unknown command line option: " + arg);
        usage();

      } else {
        err.println("** Spurious command line: " + arg);
        usage();
      }
    }
  }

}
