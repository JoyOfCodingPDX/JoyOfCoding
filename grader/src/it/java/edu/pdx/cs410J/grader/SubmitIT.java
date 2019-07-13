package edu.pdx.cs410J.grader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SubmitIT extends EmailSenderIntegrationTestCase {

  private final String studentEmail = "student@email.com";
  private final String studentName = "Student Name";
  private final Collection<File> filesToSubmit = new ArrayList<>();
  private final String studentLoginId = "student";
  private final String projectName = "Project";

  @Before
  public void createFilesToSubmit() throws IOException {
    File dir = createDirectories("edu", "pdx", "cs410J", studentLoginId);
    List<String> fileNames = Arrays.asList(projectName + ".java", "File1.java", "File2.java");
    for (String fileName : fileNames) {
      this.filesToSubmit.add(createEmptyFile(dir, fileName));
    }
  }

  @Override
  protected String getEmailFolderName() {
    return ProjectSubmissionsProcessor.EMAIL_FOLDER_NAME;
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

    GraderEmailAccount account = new GraderEmailAccount(emailServerHost, imapsPort, imapUserName, imapPassword, true);
    FetchAndProcessGraderEmail.fetchAndProcessGraderEmails("projects", account, this.tempDirectory, gradeBook);

    Grade grade = gradeBook.getStudent(studentLoginId).get().getGrade(projectName);
    assertThat(grade, is(notNullValue()));
    assertThat(grade.getScore(), equalTo(Grade.NO_GRADE));
    assertThat(grade.getSubmissionTimes().size(), equalTo(1));
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

    for (File file : filesToSubmit) {
      submit.addFile(file.getAbsolutePath());
    }

    submit.setEmailServerHostName(emailServerHost);
    submit.setEmailServerPort(smtpPort);
    submit.setDebug(true);
    submit.setSendReceipt(sendReceipt);
    submit.submit(false);
  }

  @Test
  public void submissionEmailIsSentByToGraderAndReplyToStudent() throws IOException, MessagingException {
    submitFiles();

    List<Message> messages = new ArrayList<>();

    GraderEmailAccount account = new GraderEmailAccount(emailServerHost, imapsPort, imapUserName, imapPassword, true) {
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
    assertThat(to.getAddress(), equalTo(EmailSender.TA_EMAIL.getAddress()));
    assertThat(to.getPersonal(), equalTo(EmailSender.TA_EMAIL.getPersonal()));

    InternetAddress replyTo = ((InternetAddress[]) message.getReplyTo())[0];
    assertThat(replyTo.getAddress(), equalTo(studentEmail));
    assertThat(replyTo.getPersonal(), equalTo(studentName));
  }
}
