package edu.pdx.cs410J.grader;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * This class is used to submit assignments in CS410J.  The user
 * specified his or her email address as well as the base directory
 * for his/her source files on the command line.  The directory is
 * searched recursively for files ending in .java.  Those files are
 * placed in a jar file and emailed to the grader.  A confirmation
 * email is sent to the submitter.
 *
 * More information about the JavaMail API can be found at:
 *
 * <center><A href="http://java.sun.com/products/javamail">
 * http://java.sun.com/products/javamail</A></center>
 *
 * @author David Whitlock
 */
public class Submit {

  private static PrintWriter out = new PrintWriter(System.out, true);
  private static PrintWriter err = new PrintWriter(System.err, true);
  private static final String MANIFEST = JarFile.MANIFEST_NAME;
  private static final String MANIFEST_DIR = "META-INF/";

  private static String projName = null;
  private static String userName = null;
  private static String userEmail = null;
  private static String userId = null;
  private static String serverName = "mailhost.pdx.edu";
  private static boolean DEBUG = false;
  private static boolean SAVEJAR = false;
  private static Date submitTime = null;

  private static final String TA_EMAIL = "sjavata@cs.pdx.edu";
  private static final String NO_SUBMIT_LIST_URL =
    "http://www.cs.pdx.edu/~whitlock/no-submit";

  /**
   * Prints usage information about this program.
   */
  private static void usage() {
    err.println("usage: java Submit [options] files");
    err.println("  Where [options] are:");
    err.println("  -project proj      What project is being " +
		"submitted");
    err.println("  -student name      Who is submitting the project");
    err.println("  -loginId id        UNIX login id");
//     err.println("  -sourcedir dir     Where to look for .java files"); 
    err.println("  -email email       Student's email address");
    err.println("  -savejar           Saves temporary Jar file " +
		"(optional)");
    err.println("  -smtp serverName   Name of SMTP server " +
		"(optional)");
    err.println("  -verbose           Debugging output (optional)");
    System.exit(1);
  }

  /**
   * Prints debugging output.
   */
  private static void db(String s) {
    if(DEBUG) {
      err.println("++ " + s);
    }
  }

  /**
   * Searches for the files given on the command line.  Ignores files
   * that do not end in .java, or that appear on the "no subit" list.
   * Files must reside in a directory named
   * edu/pdx/cs410J/<studentId>.
   */
  private static Set searchForSourceFiles(Set fileNames) {
    // Compute the "no submit" list
    Set noSubmit = new HashSet();

    try {
      URL url = new URL(NO_SUBMIT_LIST_URL);
      InputStreamReader isr = new InputStreamReader(url.openStream());
      BufferedReader br = new BufferedReader(isr);
      while(br.ready()) {
        noSubmit.add(br.readLine().trim());
      }

    } catch(MalformedURLException ex) {
      err.println("** WARNING: Cannot access \"no submit\" list: " +
                  ex.getMessage());

    } catch(IOException ex) {
      err.println("** WARNING: Problems while reading " + 
                  "\"no submit\" list: " + ex);
    }

    // Files should be sorted by name
    Set files = new TreeSet(new Comparator() {
	public int compare(Object o1, Object o2) {
	  if((o1 instanceof File) && (o2 instanceof File)) {
	    String name1 = o1.toString();
	    String name2 = o2.toString();

	    return(name1.compareTo(name2));

	  } else {
	    String m = "Cannot compare a " + o1.getClass() + " and a " +
	      o2.getClass();
	    throw new IllegalArgumentException(m);
	  }
	}
      });

    Iterator iter = fileNames.iterator();
    while(iter.hasNext()) {
      String fileName = (String) iter.next();
      File file = new File(fileName);
      file = file.getAbsoluteFile();  // Full path
      
      // Does the file exist?
      if(!file.exists()) {
        err.println("** Not submitting file " + fileName + 
                    " because it does not exist");
        continue;
      }

      // Is the file on the "no submit" list?
      String name = file.getName();
      if(noSubmit.contains(name)) {
        err.println("** Not submitting file " + fileName + 
                    " because it is on the \"no submit\" list");
        continue;
      }

      // Does the file name end in .java?
      if(!name.endsWith(".java")) {
        err.println("** No submitting file " + fileName + 
                    " because does end in \".java\"");
        continue;
      }

      // Verify that file is in the correct directory.
      File parent = file.getParentFile();
      if(parent == null || !parent.getName().equals(userId)) {
        err.println("** Not submitting file " + fileName + 
                    ": it does not reside in a directory named " +
                    userId);
        continue;
      }

      parent = parent.getParentFile();
      if(parent == null || !parent.getName().equals("cs410J")) {
        err.println("** Not submitting file " + fileName + 
                    ": it does not reside in a directory named " +
                    "cs410J" + File.separator + userId);
        continue;
      }

      parent = parent.getParentFile();
      if(parent == null || !parent.getName().equals("pdx")) {
        err.println("** Not submitting file " + fileName + 
                    ": it does not reside in a directory named " +
                    "pdx" + File.separator + "cs410J" + File.separator
                    + userId);
        continue;
      }

      parent = parent.getParentFile();
      if(parent == null || !parent.getName().equals("edu")) {
        err.println("** Not submitting file " + fileName + 
                    ": it does not reside in a directory named " +
                    "edu" + File.separator + "pdx" + File.separator +
                    "cs410J" + File.separator + userId);
        continue;
      }

      // We like this file
      files.add(file);
    }

    return(files);
  }

  /**
   * Prints a summary of what is about to be submitted and prompts the
   * user to verify that it is correct.
   *
   * @return <code>true</code> if the user wants to submit
   */
  private static boolean verifySubmission(Set sourceFiles) {
    // Print out what is going to be submitted
    out.print("\n" + userName);
    out.print("'s submission for ");
    out.println(projName);

    Iterator iter = sourceFiles.iterator();
    while(iter.hasNext()) {
      File file = (File) iter.next();
      out.println("  " + file);
    }

    out.println("A receipt will be sent to: " + userEmail + "\n");

    InputStreamReader isr = new InputStreamReader(System.in);
    BufferedReader in = new BufferedReader(isr);
  
    while(true) {
      out.print("Do you wish to continue with the submission? (yes/no) ");
      out.flush();

      try {
	String line = in.readLine().trim();
	if(line.equals("yes")) {
	  return(true);

	} else if(line.equals("no")) {
	  return(false);

	} else {
	  err.println("** Please enter yes or no");
	}

      } catch(IOException ex) {
	err.println("** Exception while reading from System.in: " + ex);
      }
    }
  }

  /**
   * Helper method that conversion ints to Strings.  If the int is
   * only one digit, it prepends it with a 0.
   */
  private static String doCal(int i) {
    if(i < 10) {
      return("0" + i);
    } else {
      return("" + i);
    }
  }

  /**
   * Returns the name of a <code>File</code> relative to the source
   * directory.
   */
  private static String getRelativeName(File file) {
    // We already know that the file is in the correct directory
    return("edu/pdx/cs410J/" + userId + "/" + file.getName());
  }

  /**
   * Creates a Jar file that contains the source files.  The Jar File
   * is temporary and is deleted when the program exits.
   */
  private static File makeJarFileWith(Set sourceFiles) {
    String jarFileName = userName.replace(' ', '_') + "-TEMP";
    File jarFile = null;
    try {
      jarFile = File.createTempFile(jarFileName, ".jar");
      if(!SAVEJAR) {
	jarFile.deleteOnExit();

      } else {
	out.println("Saving temporary Jar file: " + jarFile);
      }

    } catch(IOException ex) {
      err.println("** Could not create Jar file: " + jarFileName +
		  ".jar: " + ex);
      System.exit(1);
    }
    db("Created Jar file: " + jarFile);

    // Create a Manifest for the Jar file containing the name of the
    // author (userName) and a version that is based on the current
    // date/time.
    Manifest manifest = new Manifest();
    Attributes attrs = manifest.getMainAttributes();
    attrs.put(new Attributes.Name("Created-By"), userName);

    // Make a timestamp String
    Calendar cal = Calendar.getInstance();
    cal.setTime(submitTime);
    StringBuffer sb = new StringBuffer();
    sb.append(doCal(cal.get(Calendar.DAY_OF_MONTH)));
    sb.append(doCal(cal.get(Calendar.MONTH) + 1));  // Grumble
    sb.append(doCal(cal.get(Calendar.YEAR)));
    sb.append(doCal(cal.get(Calendar.HOUR_OF_DAY)));
    sb.append(doCal(cal.get(Calendar.MINUTE)));
    attrs.put(Attributes.Name.MANIFEST_VERSION, sb.toString());

    db("Jar file version: " + sb);

    // Create a JarOutputStream around the jar file
    JarOutputStream jos = null;
    try {
      OutputStream os = new FileOutputStream(jarFile);
      jos = new JarOutputStream(os, manifest);
      jos.setMethod(JarOutputStream.DEFLATED);

    } catch(IOException ex) {
      err.println("** Exception while opening JarOutputStream: " +
		  ex);
      System.exit(1);
    }

    // Add the source files to the Jar
    Iterator iter = sourceFiles.iterator();
    while(iter.hasNext()) {
      try {
	File file = (File) iter.next();

	String fileName = getRelativeName(file);
	out.println("Adding " + fileName + " to jar");
        JarEntry entry = new JarEntry(fileName);
        entry.setTime(file.lastModified());
        entry.setSize(file.length());

        InputStream is =
          new BufferedInputStream(new FileInputStream(file));
        byte[] buffer = new byte[1024];
        int read = 0;

	entry.setMethod(JarEntry.DEFLATED);

        // Add the entry to the JAR file
        jos.putNextEntry(entry);
        is = new BufferedInputStream(new FileInputStream(file));
        while((read = is.read(buffer, 0, buffer.length)) != -1) {
          jos.write(buffer, 0, read);
        }
        is.close();
        jos.closeEntry();

      } catch(IOException ex) {
        err.println("** IOException: " + ex.getMessage());
        System.exit(1);
      }
    }

    return(jarFile);
  }

  /**
   * Sends the Jar file to the TA as a MIME attachment.  Also includes
   * a textual summary of the contents of the Jar file.
   */
  private static void mailTA(File jarFile, Set sourceFiles) {
    // Obtain a Session for sending email
    Properties props = new Properties();
    props.put("mail.smtp.host", serverName);
    db("Establishing session on " + serverName);
    Session session = Session.getDefaultInstance(props, null);
    session.setDebug(DEBUG);

    // Make a new email message
    MimeMessage message = new MimeMessage(session);

    try {
      InternetAddress[] to = {new InternetAddress(TA_EMAIL)};
      message.setRecipients(Message.RecipientType.TO, to);

      StringBuffer subject = new StringBuffer();
      subject.append("CS410J-SUBMIT ");
      subject.append(userName);
      subject.append("'s ");
      subject.append(projName);
      message.setSubject(subject.toString());

    } catch(AddressException ex) {
      err.println("** Exception with TA's email (" + TA_EMAIL + "): "
		  + ex);
      System.exit(1);

    } catch(MessagingException ex) {
      err.println("** Exception while setting recipients of TA " +
		  "email: " + ex);
      System.exit(1);
    }

    // Create the text portion of the message
    StringBuffer text = new StringBuffer();
    text.append("Student name: " + userName+ " (" + userEmail + ")\n");
    text.append("Project name: " + projName + "\n");
    DateFormat df = 
      DateFormat.getDateTimeInstance(DateFormat.FULL,
				     DateFormat.FULL);
    text.append("Submitted on: " + df.format(submitTime) + "\n");
    text.append("Contents:\n");
    
    Iterator iter = sourceFiles.iterator();
    while(iter.hasNext()) {
      File file = (File) iter.next();
      text.append("  " + getRelativeName(file) + "\n");
    }
    text.append("\n\n");

    MimeBodyPart textPart = new MimeBodyPart();
    try {
      textPart.setContent(text.toString(), "text/plain");

      // Try not to display text as separate attachment
      textPart.setDisposition("inline");

    } catch(MessagingException ex) {
      err.println("** Exception with text part: " + ex);
      System.exit(1);
    }

    // Now attach the Jar file
    DataSource ds = new FileDataSource(jarFile);
    DataHandler dh = new DataHandler(ds);
    MimeBodyPart filePart = new MimeBodyPart();
    try {
      String jarFileTitle = userName.replace(' ', '_') + ".jar";

      filePart.setDataHandler(dh);
      filePart.setFileName(jarFileTitle);
      filePart.setDescription(userName + "'s " + projName);

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

  /**
   * Sends a email to the user as a receipt of the submission.
   */
  private static void mailReceipt(Set sourceFiles) {
    // Obtain a Session for sending email
    Properties props = new Properties();
    props.put("mail.smtp.host", serverName);
    db("Establishing session on " + serverName);
    Session session = Session.getDefaultInstance(props, null);
    session.setDebug(DEBUG);

    // Make a new email message
    MimeMessage message = new MimeMessage(session);

    try {
      InternetAddress[] to = {new InternetAddress(userEmail)};
      message.setRecipients(Message.RecipientType.TO, to);

      StringBuffer subject = new StringBuffer();
      subject.append("CS410J ");
      subject.append(projName);
      subject.append(" submission");
      message.setSubject(subject.toString());

    } catch(AddressException ex) {
      err.println("** Exception with user's email (" + userEmail + 
		  "): " + ex);
      System.exit(1);

    } catch(MessagingException ex) {
      err.println("** Exception with setting subject: " + ex);
      System.exit(1);
    }

    // Create the contents of the message
    DateFormat df = 
      DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
    StringBuffer text = new StringBuffer();
    text.append("On " + df.format(submitTime) + "\n");
    text.append(userName + " (" + userEmail + ")\n");
    text.append("submitted the following files for " + projName + ":\n");

    Iterator iter = sourceFiles.iterator();
    while(iter.hasNext()) {
      File file = (File) iter.next();
      text.append("  " + file.getAbsolutePath() + "\n");
    }
    text.append("\n\n");
    text.append("Have a nice day.");

    // Add the text to the message and send it
    try {
      message.setText(text.toString());
      message.setDisposition("inline");

      Transport.send(message);

    } catch(MessagingException ex) {
      err.println("** Exception with text part: " + ex);
      System.exit(1);
    }
  }

  /**
   * Parses the command line, finds the source files, prompts the user
   * to verify whether or not the settings are correct, and then sends
   * an email to the Grader.
   */
  public static void main(String[] args) {
    Set fileNames = new HashSet();

    // Parse the command line
    for(int i = 0; i < args.length; i++) {
      if(args[i].equals("-project")) {
	if(++i >= args.length) {
	  err.println("** No project name specified");
	  usage();
	}

	projName = args[i];

      } else if(args[i].equals("-student")) {
        if(++i >= args.length) {
          err.println("** No name specified");
          usage();
        }

        userName = args[i];

      } else if(args[i].equals("-loginId")) {
        if(++i >= args.length) {
          err.println("** No login id specified");
          usage();
        }

        userId = args[i];

      } else if(args[i].equals("-email")) {
	if(++i >= args.length) {
	  err.println("** No email address specified");
	  usage();
	}

	userEmail = args[i];

      } else if(args[i].equals("-smtp")) {
	if(++i >= args.length) {
	  err.println("** No SMTP server specified");
	  usage();
	}

	serverName = args[i];

      } else if(args[i].equals("-verbose")) {
	DEBUG = true;

      } else if(args[i].equals("-savejar")) {
	SAVEJAR = true;

      } else {
        // The name of a source file
        fileNames.add(args[i]);
      }
    }

    // Make sure that user entered enough information
    if(projName == null) {
      err.println("** Missing project name");
      usage();
    }

    if(userName == null) {
      err.println("** Missing student name");
      usage();
    }

    if(userId == null) {
      err.println("** Missing login id");
      usage();
    }

    if(userEmail == null) {
      err.println("** Missing email address");
      usage();

    } else {
      // Make sure user's email is okay
      try {
	new InternetAddress(userEmail);
	
      } catch(AddressException ex) {
	err.println("** Invalid email address: " + userEmail);
	System.exit(1);
      }
    }

    db("Command line successfully parsed.");

    // Recursively search the source directory for .java files
    Set sourceFiles = searchForSourceFiles(fileNames);

    db(sourceFiles.size() + " source files found");

    if(sourceFiles.size() == 0) {
      err.println("** No source files were found.  Exiting.");
      System.exit(1);
    }

    // Verify submission with user
    if(!verifySubmission(sourceFiles)) {
      // User does not want to submit
      return;
    }

    // Timestamp
    submitTime = new Date();

    // Create a temporary jar file to hold the source files
    File jarFile = makeJarFileWith(sourceFiles);

    // Send the jar file as an email attachment to the TA
    mailTA(jarFile, sourceFiles);

    // Send a receipt to the user
    mailReceipt(sourceFiles);

    // All done.
    out.println(projName + " submitted successfully.  Thank you.");
  }
}
