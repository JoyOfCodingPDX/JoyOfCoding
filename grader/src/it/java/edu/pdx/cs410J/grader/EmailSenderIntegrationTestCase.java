package edu.pdx.cs410J.grader;

import com.icegreen.greenmail.imap.AuthorizationException;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public abstract class EmailSenderIntegrationTestCase {
  protected final String emailServerHost = "127.0.0.1";
  protected final int smtpPort = 2525;
  protected final String imapUserName = "emailUser";
  protected final String imapPassword = "emailPassword";
  protected final int imapsPort = 9933;
  protected final File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
  protected GreenMail emailServer;

  @Before
  public void startEmailServer() throws FolderException, AuthorizationException {
    ServerSetup smtp = new ServerSetup(smtpPort, emailServerHost, ServerSetup.PROTOCOL_SMTP);
    ServerSetup imaps = new ServerSetup(imapsPort, emailServerHost, ServerSetup.PROTOCOL_IMAPS);
    emailServer = new GreenMail(new ServerSetup[]{ smtp, imaps });

    List<String> addresses = getEmailAddressesForSmtpServer();
    for (String address : addresses) {
      GreenMailUser user = emailServer.setUser(address, imapUserName, imapPassword);
      initializeSmtpUser(user);
    }

    emailServer.start();
  }

  protected abstract void initializeSmtpUser(GreenMailUser user) throws AuthorizationException, FolderException;

  protected abstract List<String> getEmailAddressesForSmtpServer();

  @After
  public void stopEmailServer() {
    emailServer.stop();
  }

  protected File createEmptyFile(File dir, String fileName) throws IOException {
    File file = new File(dir, fileName);
    FileWriter writer = new FileWriter(file);
    writer.write("");
    return file;
  }

  protected File createDirectories(String... directoryNames) {
    File dir = tempDirectory;
    for (String dirName : directoryNames) {
      dir = new File(dir, dirName);
      dir.mkdir();
    }

    return dir;
  }

}
