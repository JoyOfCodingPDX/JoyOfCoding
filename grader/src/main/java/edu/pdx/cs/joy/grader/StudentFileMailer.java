package edu.pdx.cs.joy.grader;

import com.google.common.io.Files;
import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.grader.gradebook.GradeBook;
import edu.pdx.cs.joy.grader.gradebook.Student;
import edu.pdx.cs.joy.grader.gradebook.XmlGradeBookParser;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.*;

public class StudentFileMailer extends EmailSender {
  private static Logger logger = LoggerFactory.getLogger(StudentFileMailer.class.getPackage().getName());

  private final String subject;
  private final Session session;

  public StudentFileMailer(String subject, Session session) {
    this.subject = subject;
    this.session = session;
  }

  public static void main(String[] args) {
    String gradeBookFileName = null;
    String subject = null;
    List<String> fileNames = new ArrayList<>();

    for (String arg : args) {
      if (gradeBookFileName == null) {
        gradeBookFileName = arg;

      } else if (subject == null) {
        subject = arg;

      } else {
        fileNames.add(arg);
      }
    }

    usageIfNull(gradeBookFileName, "Missing grade book file name");
    usageIfNull(subject, "Missing email subject");
    usageIfEmpty(fileNames, "Missing file names");
    List<File> files = getFiles(fileNames);
    GradeBook gradeBook = getGradeBook(gradeBookFileName);
    Map<Student, File> filesToSendToStudents = getFilesToSendToStudents(files, gradeBook);

    StudentFileMailer mailer = new StudentFileMailer(subject, newEmailSession(false));
    filesToSendToStudents.forEach((student, file) -> {
      try {
        mailer.mailFileToStudent(file, student);

      } catch (MessagingException | IOException ex) {
        logger.error("While mailing \"" + file + "\" to " + student, ex);
      }
    });
  }

  private void mailFileToStudent(File file, Student student) throws MessagingException, IOException {
    InternetAddress studentEmail = getEmailAddress(student);

    logger.info("Mailing \"" + file + "\" to \"" + studentEmail + "\"");

    MimeMessage message =
      newEmailTo(this.session, studentEmail)
        .from(TA_EMAIL)
        .replyTo(DAVE_EMAIL)
        .withSubject(this.subject)
        .createMessage();
    message.setText(readTextFromFile(file));

    sendEmailMessage(message);
  }

  private String readTextFromFile(File file) throws IOException {
    StringBuilder sb = new StringBuilder();
    Files.copy(file, Charset.defaultCharset(), sb);
    return sb.toString();
  }

  private void sendEmailMessage(MimeMessage message) throws MessagingException {
    Transport.send(message);
  }

  private InternetAddress getEmailAddress(Student student) {
    StringBuilder name = new StringBuilder();
    name.append(student.getFirstName());
    name.append(" ");
    if (student.getNickName() != null) {
      name.append("\"");
      name.append(student.getNickName());
      name.append("\" ");
    }
    name.append(student.getLastName());

    return newInternetAddress(student.getEmail(), name.toString());
  }

  private static Map<Student, File> getFilesToSendToStudents(List<File> files, GradeBook gradeBook) {
    Map<Student, File> filesToSendToStudents = new HashMap<>();
    for (File file : files) {
      String studentId = getStudentIdFromFileName(file);
      Optional<Student> maybeStudent = gradeBook.getStudent(studentId);
      if (maybeStudent.isEmpty()) {
        cannotFindStudent(studentId);
      } else {
        filesToSendToStudents.put(maybeStudent.get(), file);
      }
    }

    return filesToSendToStudents;
  }

  private static void cannotFindStudent(String studentId) {
    logger.warn("Cannot find student with id \"" + studentId + "\" in grade book");
  }

  private static String getStudentIdFromFileName(File file) {
    String studentId;
    String fileName = file.getName();
    int index = fileName.lastIndexOf('.');
    if (index < 0) {
      studentId = fileName;

    } else {
      studentId = fileName.substring(0, index);
    }
    return studentId;
  }

  private static List<File> getFiles(List<String> fileNames) {
    List<File> files = new ArrayList<>(fileNames.size());
    for (String fileName : fileNames) {
      File file = new File(fileName);
      if (!file.exists()) {
        throw new IllegalStateException("File \"" + fileName + "\" does not exist");

      } else if (!file.isFile()) {
        throw new IllegalStateException("File \"" + fileName + "\" is not a file");
      }

      files.add(file);
    }

    return files;
  }

  private static GradeBook getGradeBook(String gradeBookFileName) {
    try {
      XmlGradeBookParser parser = new XmlGradeBookParser(gradeBookFileName);
      return parser.parse();

    } catch (IOException ex) {
      return usage("Couldn't read grade book file \"" + gradeBookFileName + "\"");

    } catch (ParserException ex) {
      return usage("Couldn't parse grade book file \"" + gradeBookFileName + "\"");
    }
  }


  private static void usageIfEmpty(List<String> list, String message) {
    if (list.isEmpty()) {
      usage(message);
    }
  }

  private static void usageIfNull(String arg, String message) {
    if (arg == null) {
      usage(message);
    }
  }

  private static <T> T usage(String message) {
    PrintStream err = System.err;

    err.println("** " + message);
    err.println();
    err.println("usage: java StudentFileMailer gradeBookFileName subject fileName+");
    err.println();
    err.println("Emails a file to a student in a grade book.  The name of the file must");
    err.println("begin with the student's id.");
    err.println();
    err.println("  gradeBookFileName    Name of the grade book file");
    err.println("  subject              The subject of the email");
    err.println("  fileName             A file to send to the student");
    err.println();

    System.exit(1);

    throw new UnsupportedOperationException("Should never get here");
  }
}
