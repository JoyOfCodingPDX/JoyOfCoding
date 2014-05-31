package edu.pdx.cs410J.grader;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * This class is used to submit assignments in CS410J.  The user
 * specified his or her email address as well as the base directory
 * for his/her source files on the command line.  The directory is
 * searched recursively for files ending in .java.  Those files are
 * placed in a jar file and emailed to the grader.  A confirmation
 * email is sent to the submitter.
 * <p/>
 * <p/>
 * <p/>
 * More information about the JavaMail API can be found at:
 * <p/>
 * <center><a href="http://java.sun.com/products/javamail">
 * http://java.sun.com/products/javamail</a></center>
 *
 * @author David Whitlock
 * @since Fall 2000 (Refactored to use fewer static methods in Spring 2006)
 */
public class Submit {

  private static final PrintWriter out = new PrintWriter(System.out, true);
  private static final PrintWriter err = new PrintWriter(System.err, true);

  /**
   * The grader's email address
   */
  private static final String TA_EMAIL = "sjavata@gmail.com";

  /**
   * A URL containing a list of files that should not be submitted
   */
  private static final String NO_SUBMIT_LIST_URL =
    "http://www.cs.pdx.edu/~whitlock/no-submit";

  /////////////////////  Instance Fields  //////////////////////////

  /**
   * The name of the project being submitted
   */
  private String projName = null;

  /**
   * The name of the user (student) submits the project
   */
  private String userName = null;

  /**
   * The submitter's email address
   */
  private String userEmail = null;

  /**
   * The submitter's user id
   */
  private String userId = null;

  /**
   * The name of the SMTP server that is used to send email
   */
  private String serverName = "mailhost.cs.pdx.edu";

  /**
   * A comment describing the project
   */
  private String comment = null;

  /**
   * Should the execution of this program be logged?
   */
  private boolean debug = false;

  /**
   * Should the generated jar file be saved?
   */
  private boolean saveJar = false;

  /**
   * The time at which the project was submitted
   */
  private Date submitTime = null;

  /**
   * The names of the files to be submitted
   */
  private Set<String> fileNames = new HashSet<>();

  ///////////////////////  Constructors  /////////////////////////

  /**
   * Creates a new <code>Submit</code> program
   */
  public Submit() {

  }

  /////////////////////  Instance Methods  ///////////////////////

  /**
   * Sets the name of the SMTP server that is used to send emails
   */
  public void setServerName(String serverName) {
    this.serverName = serverName;
  }

  /**
   * Sets whether or not the progress of the submission should be logged.
   */
  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  /**
   * Sets whether or not the jar file generated by the submission
   * should be saved.
   */
  public void setSaveJar(boolean saveJar) {
    this.saveJar = saveJar;
  }

  /**
   * Sets the comment for this submission
   */
  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * Sets the name of project being submitted
   */
  public void setProjectName(String projName) {
    this.projName = projName;
  }

  /**
   * Sets the name of the user who is submitting the project
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * Sets the id of the user who is submitting the project
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * Sets the email address of the user who is submitting the project
   */
  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  /**
   * Adds the file with the given name to the list of files to be
   * submitted.
   */
  public void addFile(String fileName) {
    this.fileNames.add(fileName);
  }

  /**
   * Validates the state of this submission
   *
   * @throws IllegalStateException If any state is incorrect or missing
   */
  public void validate() {
    if (projName == null) {
      throw new IllegalStateException("Missing project name");
    }

    if (userName == null) {
      throw new IllegalStateException("Missing student name");
    }

    if (userId == null) {
      throw new IllegalStateException("Missing login id");
    }

    if (userEmail == null) {
      throw new IllegalStateException("Missing email address");

    } else {
      // Make sure user's email is okay
      try {
        new InternetAddress(userEmail);

      } catch (AddressException ex) {
        String s = "Invalid email address: " + userEmail;
        IllegalStateException ex2 = new IllegalStateException(s);
        ex2.initCause(ex);
        throw ex2;
      }
    }
  }

  /**
   * Submits the project to the grader
   *
   * @param verify Should the user be prompted to verify his submission?
   * @return Whether or not the submission actually occurred
   * @throws IllegalStateException If no source files were found
   */
  public boolean submit(boolean verify) throws IOException, MessagingException {
    // Recursively search the source directory for .java files
    Set<File> sourceFiles = searchForSourceFiles(fileNames);

    db(sourceFiles.size() + " source files found");

    if (sourceFiles.size() == 0) {
      String s = "No source files were found.";
      throw new IllegalStateException(s);
    }

    // Verify submission with user
    if (verify && !verifySubmission(sourceFiles)) {
      // User does not want to submit
      return false;
    }

    // Timestamp
    this.submitTime = new Date();

    // Create a temporary jar file to hold the source files
    File jarFile = makeJarFileWith(sourceFiles);

    // Send the jar file as an email attachment to the TA
    mailTA(jarFile, sourceFiles);

    // Send a receipt to the user
    mailReceipt(sourceFiles);

    return true;
  }

  /**
   * Prints debugging output.
   */
  private void db(String s) {
    if (this.debug) {
      err.println("++ " + s);
    }
  }

  /**
   * Searches for the files given on the command line.  Ignores files
   * that do not end in .java, or that appear on the "no submit" list.
   * Files must reside in a directory named
   * edu/pdx/cs410J/<studentId>.
   */
  private Set<File> searchForSourceFiles(Set<String> fileNames) {
    Set<String> noSubmit = fetchListOfFilesThatCanNotBeSubmitted();

    // Files should be sorted by name
    SortedSet<File> files = new TreeSet<>(new Comparator<File>() {
      @Override
      public int compare(File o1, File o2) {
        String name1 = o1.toString();
        String name2 = o2.toString();

        return name1.compareTo(name2);
      }
    });

    for (String fileName : fileNames) {
      File file = new File(fileName);
      file = file.getAbsoluteFile();  // Full path

      // Does the file exist?
      if (!file.exists()) {
        err.println("** Not submitting file " + fileName +
          " because it does not exist");
        continue;
      }

      // Is the file on the "no submit" list?
      String name = file.getName();
      if (noSubmit.contains(name)) {
        err.println("** Not submitting file " + fileName +
          " because it is on the \"no submit\" list");
        continue;
      }

      // Does the file name end in .java?
      if (!name.endsWith(".java")) {
        err.println("** No submitting file " + fileName +
          " because does end in \".java\"");
        continue;
      }

      // Verify that file is in the correct directory.
      File parent = file.getParentFile();
      if (parent == null || !parent.getName().equals(userId)) {
        err.println("** Not submitting file " + fileName +
          ": it does not reside in a directory named " +
          userId);
        continue;
      }

      parent = parent.getParentFile();
      if (parent == null || !parent.getName().equals("cs410J")) {
        err.println("** Not submitting file " + fileName +
          ": it does not reside in a directory named " +
          "cs410J" + File.separator + userId);
        continue;
      }

      parent = parent.getParentFile();
      if (parent == null || !parent.getName().equals("pdx")) {
        err.println("** Not submitting file " + fileName +
          ": it does not reside in a directory named " +
          "pdx" + File.separator + "cs410J" + File.separator
          + userId);
        continue;
      }

      parent = parent.getParentFile();
      if (parent == null || !parent.getName().equals("edu")) {
        err.println("** Not submitting file " + fileName +
          ": it does not reside in a directory named " +
          "edu" + File.separator + "pdx" + File.separator +
          "cs410J" + File.separator + userId);
        continue;
      }

      // We like this file
      files.add(file);
    }

    return files;
  }

  private Set<String> fetchListOfFilesThatCanNotBeSubmitted() {
    Set<String> noSubmit = new HashSet<>();

    try {
      URL url = new URL(NO_SUBMIT_LIST_URL);
      InputStreamReader isr = new InputStreamReader(url.openStream());
      BufferedReader br = new BufferedReader(isr);
      while (br.ready()) {
        noSubmit.add(br.readLine().trim());
      }

    } catch (MalformedURLException ex) {
      err.println("** WARNING: Cannot access \"no submit\" list: " +
        ex.getMessage());

    } catch (IOException ex) {
      err.println("** WARNING: Problems while reading " +
        "\"no submit\" list: " + ex);
    }
    return noSubmit;
  }

  /**
   * Prints a summary of what is about to be submitted and prompts the
   * user to verify that it is correct.
   *
   * @return <code>true</code> if the user wants to submit
   */
  private boolean verifySubmission(Set<File> sourceFiles) {
    // Print out what is going to be submitted
    out.print("\n" + userName);
    out.print("'s submission for ");
    out.println(projName);

    for (File file : sourceFiles) {
      out.println("  " + file);
    }

    if (comment != null) {
      out.println("\nComment: " + comment + "\n\n");
    }

    out.println("A receipt will be sent to: " + userEmail + "\n");

    InputStreamReader isr = new InputStreamReader(System.in);
    BufferedReader in = new BufferedReader(isr);

    while (true) {
      out.print("Do you wish to continue with the submission? (yes/no) ");
      out.flush();

      try {
        String line = in.readLine().trim();
        switch (line) {
          case "yes":
            return true;

          case "no":
            return false;

          default:
            err.println("** Please enter yes or no");
            break;
        }

      } catch (IOException ex) {
        err.println("** Exception while reading from System.in: " + ex);
      }
    }
  }

  /**
   * Helper method that conversion ints to Strings.  If the int is
   * only one digit, it prepends it with a 0.
   */
  private String padWithZero(int i) {
    if (i < 10) {
      return "0" + i;
    } else {
      return String.valueOf(i);
    }
  }

  /**
   * Returns the name of a <code>File</code> relative to the source
   * directory.
   */
  private String getRelativeName(File file) {
    // We already know that the file is in the correct directory
    return "edu/pdx/cs410J/" + userId + "/" + file.getName();
  }

  /**
   * Creates a Jar file that contains the source files.  The Jar File
   * is temporary and is deleted when the program exits.
   */
  private File makeJarFileWith(Set<File> sourceFiles) throws IOException {
    String jarFileName = userName.replace(' ', '_') + "-TEMP";
    File jarFile;
    {
      jarFile = File.createTempFile(jarFileName, ".jar");
      if (!saveJar) {
        jarFile.deleteOnExit();

      } else {
        out.println("Saving temporary Jar file: " + jarFile);
      }
    }
    db("Created Jar file: " + jarFile);

    String createdBy = userName;
    String manifestVersion = getTimestampString();

    db("Jar file version: " + manifestVersion);

    Map<File, String> sourceFilesWithNames = Maps.toMap(sourceFiles, new Function<File, String>() {
      @Override
      public String apply(File file) {
        return getRelativeName(file);
      }
    });

    return new JarMaker(sourceFilesWithNames, jarFile, createdBy, manifestVersion).makeJar();
  }

  private String getTimestampString() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(submitTime);
    return padWithZero(cal.get(Calendar.DAY_OF_MONTH)) +
      padWithZero(cal.get(Calendar.MONTH) + 1) +
      padWithZero(cal.get(Calendar.YEAR)) +
      padWithZero(cal.get(Calendar.HOUR_OF_DAY)) +
      padWithZero(cal.get(Calendar.MINUTE));
  }

  /**
   * Sends the Jar file to the TA as a MIME attachment.  Also includes
   * a textual summary of the contents of the Jar file.
   */
  private void mailTA(File jarFile, Set<File> sourceFiles) throws MessagingException {
    // Obtain a Session for sending email
    Properties props = new Properties();
    props.put("mail.smtp.host", serverName);
    db("Establishing session on " + serverName);
    Session session = Session.getDefaultInstance(props, null);
    session.setDebug(this.debug);

    // Make a new email message
    MimeMessage message = new MimeMessage(session);

    {
      InternetAddress[] to = {new InternetAddress(TA_EMAIL)};
      message.setRecipients(Message.RecipientType.TO, to);

      message.setSubject("CS410J-SUBMIT " + userName + "'s " + projName);
    }

    // Create the text portion of the message
    StringBuilder text = new StringBuilder();
    text.append("Student name: ").append(userName).append(" (").append(userEmail).append(")\n");
    text.append("Project name: ").append(projName).append("\n");
    DateFormat df =
      DateFormat.getDateTimeInstance(DateFormat.FULL,
        DateFormat.FULL);
    text.append("Submitted on: ").append(df.format(submitTime)).append("\n");
    if (comment != null) {
      text.append("\nComment: ").append(comment).append("\n\n");
    }
    text.append("Contents:\n");

    for (File file : sourceFiles) {
      text.append("  ").append(getRelativeName(file)).append("\n");
    }
    text.append("\n\n");

    MimeBodyPart textPart = new MimeBodyPart();
    {
      textPart.setContent(text.toString(), "text/plain");

      // Try not to display text as separate attachment
      textPart.setDisposition("inline");
    }

    // Now attach the Jar file
    DataSource ds = new FileDataSource(jarFile);
    DataHandler dh = new DataHandler(ds);
    MimeBodyPart filePart = new MimeBodyPart();
    {
      String jarFileTitle = userName.replace(' ', '_') + ".jar";

      filePart.setDataHandler(dh);
      filePart.setFileName(jarFileTitle);
      filePart.setDescription(userName + "'s " + projName);
    }

    // Finally, add the attachments to the message and send it
    {
      Multipart mp = new MimeMultipart();
      mp.addBodyPart(textPart);
      mp.addBodyPart(filePart);

      message.setContent(mp);

      Transport.send(message);
    }
  }

  /**
   * Sends a email to the user as a receipt of the submission.
   */
  private void mailReceipt(Set<File> sourceFiles) throws MessagingException {
    // Obtain a Session for sending email
    Properties props = new Properties();
    props.put("mail.smtp.host", serverName);
    db("Establishing session on " + serverName);
    Session session = Session.getDefaultInstance(props, null);
    session.setDebug(this.debug);

    // Make a new email message
    MimeMessage message = new MimeMessage(session);

    {
      InternetAddress[] to = {new InternetAddress(userEmail)};
      message.setRecipients(Message.RecipientType.TO, to);

      message.setSubject("CS410J " + projName + " submission");
    }

    // Create the contents of the message
    DateFormat df =
      DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
    StringBuilder text = new StringBuilder();
    text.append("On ").append(df.format(submitTime)).append("\n");
    text.append(userName).append(" (").append(userEmail).append(")\n");
    text.append("submitted the following files for ").append(projName).append(":\n");

    for (File file : sourceFiles) {
      text.append("  ").append(file.getAbsolutePath()).append("\n");
    }

    if (comment != null) {
      text.append("\nComment: ").append(comment).append("\n\n");
    }

    text.append("\n\n");
    text.append("Have a nice day.");

    // Add the text to the message and send it
    {
      message.setText(text.toString());
      message.setDisposition("inline");

      Transport.send(message);
    }
  }

  /////////////////////////  Main Program  ///////////////////////////

  /**
   * Prints usage information about this program.
   */
  private static void usage(String s) {
    err.println("\n** " + s + "\n");
    err.println("usage: java Submit [options] args file+");
    err.println("  args are (in this order):");
    err.println("    project      What project is being submitted");
    err.println("    student      Who is submitting the project?");
    err.println("    loginId      UNIX login id");
    err.println("    email        Student's email address");
    err.println("    file         Java source file to submit");
    err.println("  options are (options may appear in any order):");
    err.println("    -savejar           Saves temporary Jar file");
    err.println("    -smtp serverName   Name of SMTP server");
    err.println("    -verbose           Log debugging output");
    err.println("    -comment comment   Info for the Grader");
    err.println("");
    err.println("Submits Java source code to the CS410J grader.");
    System.exit(1);
  }

  /**
   * Parses the command line, finds the source files, prompts the user
   * to verify whether or not the settings are correct, and then sends
   * an email to the Grader.
   */
  public static void main(String[] args) throws IOException, MessagingException {
    Submit submit = new Submit();

    // Parse the command line
    for (int i = 0; i < args.length; i++) {
      // Check for options first
      if (args[i].equals("-smtp")) {
        if (++i >= args.length) {
          usage("No SMTP server specified");
        }

        submit.setServerName(args[i]);

      } else if (args[i].equals("-verbose")) {
        submit.setDebug(true);

      } else if (args[i].equals("-savejar")) {
        submit.setSaveJar(true);

      } else if (args[i].equals("-comment")) {
        if (++i >= args.length) {
          usage("No comment specified");
        }

        submit.setComment(args[i]);

      } else if (submit.projName == null) {
        submit.setProjectName(args[i]);

      } else if (submit.userName == null) {
        submit.setUserName(args[i]);

      } else if (submit.userId == null) {
        submit.setUserId(args[i]);

      } else if (submit.userEmail == null) {
        submit.setUserEmail(args[i]);

      } else {
        // The name of a source file
        submit.addFile(args[i]);
      }
    }

    boolean submitted;

    try {
      // Make sure that user entered enough information
      submit.validate();

      submit.db("Command line successfully parsed.");

      submitted = submit.submit(true);

    } catch (IllegalStateException ex) {
      usage(ex.getMessage());
      return;
    }

    // All done.
    if (submitted) {
      out.println(submit.projName + " submitted successfully.  Thank you.");

    } else {
      out.println(submit.projName + " not submitted.");
    }
  }

  private static class JarMaker {
    private Map<File, String> sourceFilesAndNames;
    private File jarFile;
    private String createdBy;
    private String manifestVersion;

    public JarMaker(Map<File, String> sourceFilesAndNames, File jarFile, String createdBy, String manifestVersion) {
      this.sourceFilesAndNames = sourceFilesAndNames;
      this.jarFile = jarFile;
      this.createdBy = createdBy;
      this.manifestVersion = manifestVersion;
    }

    public File makeJar() throws IOException {
      // Create a Manifest for the Jar file containing the name of the
      // author (userName) and a version that is based on the current
      // date/time.
      Manifest manifest = new Manifest();
      Attributes attrs = manifest.getMainAttributes();
      attrs.put(new Attributes.Name("Created-By"), createdBy);

      attrs.put(Attributes.Name.MANIFEST_VERSION, manifestVersion);

      // Create a JarOutputStream around the jar file
      JarOutputStream jos;
      {
        OutputStream os = new FileOutputStream(jarFile);
        jos = new JarOutputStream(os, manifest);
        jos.setMethod(JarOutputStream.DEFLATED);
      }

      // Add the source files to the Jar
      for (Map.Entry<File, String> fileEntry : sourceFilesAndNames.entrySet()) {
        {
          File file = fileEntry.getKey();
          String fileName = fileEntry.getValue();
          System.out.println("Adding " + fileName + " to jar");
          JarEntry entry = new JarEntry(fileName);
          entry.setTime(file.lastModified());
          entry.setSize(file.length());

          InputStream is;
          byte[] buffer = new byte[1024];
          int read;

          entry.setMethod(JarEntry.DEFLATED);

          // Add the entry to the JAR file
          jos.putNextEntry(entry);
          is = new BufferedInputStream(new FileInputStream(file));
          while ((read = is.read(buffer, 0, buffer.length)) != -1) {
            jos.write(buffer, 0, read);
          }
          is.close();
          jos.closeEntry();
        }
      }

      return jarFile;
    }
  }
}
