package edu.pdx.cs410J.grader;

import com.icegreen.greenmail.imap.AuthorizationException;
import com.icegreen.greenmail.imap.ImapHostManager;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.store.FolderListener;
import com.icegreen.greenmail.store.MailFolder;
import com.icegreen.greenmail.user.GreenMailUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static edu.pdx.cs410J.grader.EmailSender.DAVE_EMAIL;
import static edu.pdx.cs410J.grader.EmailSender.TA_EMAIL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SubmitIT extends EmailSenderIntegrationTestCase {

  private final String studentEmail = "student@email.com";
  private final String studentName = "Student Name";
  private final Collection<File> filesToSubmit = new ArrayList<>();
  private final String studentLoginId = "student";
  private final String projectName = "Project";
  private String graderEmail = TA_EMAIL.getAddress();

  @Before
  public void createFilesToSubmit() throws IOException {
    File mainDir = createDirectories("src", "main", "java", "edu", "pdx", "cs410J", studentLoginId);
    for (String fileName : Arrays.asList(projectName + ".java", "File1.java", "File2.java")) {
      this.filesToSubmit.add(createEmptyFile(mainDir, fileName));
    }

    File mainJavaDocDir = createDirectories("src", "main", "javadoc", "edu", "pdx", "cs410J", studentLoginId);
    this.filesToSubmit.add(createEmptyFile(mainJavaDocDir, "package.html"));

    File testDir = createDirectories("src", "test", "java", "edu", "pdx", "cs410J", studentLoginId);
    for (String fileName : Arrays.asList(projectName + "Test.java", "File1Test.java", "File2Test.java")) {
      this.filesToSubmit.add(createEmptyFile(testDir, fileName));
    }

    File testJavaDocDir = createDirectories("src", "test", "javadoc", "edu", "pdx", "cs410J", studentLoginId);
    this.filesToSubmit.add(createEmptyFile(testJavaDocDir, "package.html"));

    File testResourcesDir = createDirectories("src", "test", "resources", "edu", "pdx", "cs410J", studentLoginId);
    this.filesToSubmit.add(createEmptyFile(testResourcesDir, "testData.xml"));

    File itDir = createDirectories("src", "it", "java", "edu", "pdx", "cs410J", studentLoginId);
    for (String fileName : Arrays.asList(projectName + "IT.java", "File1IT.java", "File2IT.java")) {
      this.filesToSubmit.add(createEmptyFile(itDir, fileName));
    }

    File itResourcesDir = createDirectories("src", "it", "resources", "edu", "pdx", "cs410J", studentLoginId);
    this.filesToSubmit.add(createEmptyFile(itResourcesDir, "testData.xml"));

  }

  @Before
  public void enableDebugLogging() {
    GraderTools.setLoggingLevelToDebug();
  }

  @After
  public void deleteFilesToSubmit() {
    filesToSubmit.forEach(File::delete);
  }

  @Test
  public void submitFilesAndDownloadSubmission() throws IOException, MessagingException {
    submitFiles();

    GradeBook gradeBook = new GradeBook("SubmitIT");
    gradeBook.addStudent(new Student(studentLoginId));
    gradeBook.addAssignment(new Assignment(projectName, 3.5));

    GraderEmailAccount account = new GraderEmailAccount(emailServerHost, imapsPort, graderEmail, imapPassword, true);
    FetchAndProcessGraderEmail.fetchAndProcessGraderEmails("projects", account, this.tempDirectory, gradeBook);

    Grade grade = gradeBook.getStudent(studentLoginId).get().getGrade(projectName);
    assertThat(grade, is(notNullValue()));
    assertThat(grade.getScore(), equalTo(Grade.NO_GRADE));
    assertThat(grade.getSubmissionTimes().size(), equalTo(1));

    assertZipFileContainsFilesInMavenProjectDirectories();
  }

  private void assertZipFileContainsFilesInMavenProjectDirectories() throws IOException {
    File zipFile = findNewestZipFileInTempDirectory();
    assertThat(zipFile, is(notNullValue()));
    List<String> entryNames = getZipFileEntryNames(zipFile);
    assertThat(entryNames, not(empty()));

    filesToSubmit.forEach(file -> {
      String filePath = file.getPath().substring(this.tempDirectory.getPath().length() + 1);
      assertThat(entryNames, hasItem(filePath));
    });
  }

  private List<String> getZipFileEntryNames(File zipFile) throws IOException {
    List<String> entryNames = new ArrayList<>();
    FileInputStream stream = new FileInputStream(zipFile);
    try (
      ZipInputStream zipStream = new ZipInputStream(stream);
    ) {
      for (ZipEntry entry = zipStream.getNextEntry(); entry != null; entry = zipStream.getNextEntry()) {
        String entryName = entry.getName();
        entryNames.add(entryName);
      }
    }

    return entryNames;
  }

  private File findNewestZipFileInTempDirectory() throws IOException {
    File[] zipFiles = this.tempDirectory.listFiles((dir, name) -> name.endsWith(".zip"));
    if (zipFiles == null) {
      return null;

    } else {
      return Collections.max(List.of(zipFiles), Comparator.comparingDouble(File::lastModified));
    }
  }

  private void submitFiles() throws IOException, MessagingException {
    submitFiles(false);
  }

  private void submitFiles(boolean sendReceipt) throws IOException, MessagingException {
    Submit submit = new Submit();
    submit.setProjectName(projectName);
    submit.setUserEmail(studentEmail);
    submit.setUserId(studentLoginId);
    submit.setUserName(studentName);
    submit.setFailIfDisallowedFiles(false);

    for (File file : filesToSubmit) {
      submit.addFile(file.getAbsolutePath());
    }

    submit.setEmailServerHostName(emailServerHost);
    submit.setEmailServerPort(smtpPort);
    submit.setDebug(true);
    submit.setSendReceipt(sendReceipt);
    submit.submit(false);
  }

  @Override
  protected void initializeSmtpUser(GreenMailUser user) throws AuthorizationException, FolderException {
    if (user.getEmail().equals(graderEmail)) {
      moveEmailsFromInboxToProjectSubmissions(user);
    }
  }

  @Override
  protected List<String> getEmailAddressesForSmtpServer() {
    return List.of(graderEmail, studentEmail);
  }

  private void moveEmailsFromInboxToProjectSubmissions(GreenMailUser user) throws AuthorizationException, FolderException {
    ImapHostManager manager = emailServer.getManagers().getImapHostManager();
    MailFolder submissions = manager.createMailbox(user, ProjectSubmissionsProcessor.EMAIL_FOLDER_NAME);
    MailFolder inbox = manager.getInbox(user);
    inbox.addListener(new FolderListener() {
      @Override
      public void expunged(int msn) {

      }

      @Override
      public void added(int msn) {
        try {
          inbox.copyMessage(msn, submissions);

        } catch (FolderException ex) {
          throw new IllegalStateException("Can't copy message to submissions folder", ex);
        }
      }

      @Override
      public void flagsUpdated(int msn, Flags flags, Long uid) {

      }

      @Override
      public void mailboxDeleted() {

      }
    });
  }

  @Test
  public void submissionEmailIsSentByToGraderAndReplyToStudent() throws IOException, MessagingException {
    submitFiles();

    List<Message> messages = new ArrayList<>();

    GraderEmailAccount account = new GraderEmailAccount(emailServerHost, imapsPort, graderEmail, imapPassword, true) {
      @Override
      protected void fetchAttachmentsFromUnreadMessage(Message message, EmailAttachmentProcessor processor) throws MessagingException, IOException {
        messages.add(message);
        super.fetchAttachmentsFromUnreadMessage(message, processor);
      }
    };

    GradeBook gradeBook = new GradeBook("SubmitIT");
    gradeBook.addStudent(new Student(studentLoginId));
    gradeBook.addAssignment(new Assignment(projectName, 3.5));

    FetchAndProcessGraderEmail.fetchAndProcessGraderEmails("projects", account, this.tempDirectory, gradeBook);

    assertThat(messages.size(), equalTo(1));
    Message message = messages.get(0);
    Address[] fromArray = message.getFrom();
    assertThat(fromArray.length, equalTo(1));
    assertThat(fromArray[0], instanceOf(InternetAddress.class));

    InternetAddress from = (InternetAddress) fromArray[0];
    assertThat(from.getAddress(), equalTo(studentEmail));
    assertThat(from.getPersonal(), equalTo(studentName));

    InternetAddress to = ((InternetAddress[]) message.getRecipients(Message.RecipientType.TO))[0];
    assertThat(to.getAddress(), equalTo(TA_EMAIL.getAddress()));
    assertThat(to.getPersonal(), equalTo(TA_EMAIL.getPersonal()));

    InternetAddress replyTo = ((InternetAddress[]) message.getReplyTo())[0];
    assertThat(replyTo.getAddress(), equalTo(studentEmail));
    assertThat(replyTo.getPersonal(), equalTo(studentName));
  }

  @Test
  public void receiptEmailRepliesToDave() throws IOException, MessagingException {
    submitFiles(true);

    List<Message> messages = new ArrayList<>();

    GraderEmailAccount studentAccount = new GraderEmailAccount(emailServerHost, imapsPort, studentEmail, imapPassword, true) {
      @Override
      protected void fetchAttachmentsFromUnreadMessage(Message message, EmailAttachmentProcessor processor) throws MessagingException, IOException {
        messages.add(message);
        super.fetchAttachmentsFromUnreadMessage(message, processor);
      }
    };

    studentAccount.fetchAttachmentsFromUnreadMessagesInFolder("inbox", new EmailAttachmentProcessor() {
      @Override
      public void processAttachment(Message message, String fileName, InputStream inputStream) {

      }

      @Override
      public Iterable<? extends String> getSupportedContentTypes() {
        return List.of();
      }
    });

    assertThat(messages.size(), equalTo(1));

    Message message = messages.get(0);

    InternetAddress from = (InternetAddress) message.getFrom()[0];
    assertThat(from.getAddress(), equalTo(TA_EMAIL.getAddress()));
    assertThat(from.getPersonal(), equalTo(TA_EMAIL.getPersonal()));

    InternetAddress to = ((InternetAddress[]) message.getRecipients(Message.RecipientType.TO))[0];
    assertThat(to.getAddress(), equalTo(studentEmail));
    assertThat(to.getPersonal(), equalTo(studentName));

    InternetAddress replyTo = ((InternetAddress[]) message.getReplyTo())[0];
    assertThat(replyTo.getAddress(), equalTo(DAVE_EMAIL.getAddress()));
    assertThat(replyTo.getPersonal(), equalTo(DAVE_EMAIL.getPersonal()));
  }

}
