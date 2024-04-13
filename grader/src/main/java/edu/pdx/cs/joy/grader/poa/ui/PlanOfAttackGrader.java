package edu.pdx.cs.joy.grader.poa.ui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import edu.pdx.cs.joy.grader.poa.DownloadPOASubmissionsRequest;
import edu.pdx.cs.joy.grader.poa.LoadGradeBook;
import edu.pdx.cs.joy.grader.poa.POASubmission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;

@Singleton
public class PlanOfAttackGrader {
  private static final Logger logger = LoggerFactory.getLogger(PlanOfAttackGrader.class);
  private final TopLevelJFrame parent;

  @Inject
  public PlanOfAttackGrader(TopLevelJFrame parent,
                            POASubmissionsPanel submissions,
                            POASubmissionInformationPanel submissionInfo,
                            StatusMessageWidget statusMessage) {
    this.parent = parent;

    parent.setTitle("Plan Of Attack Grader");

    Container content = parent.getContentPane();
    content.setLayout(new BorderLayout());
    content.add(submissions, BorderLayout.WEST);
    content.add(submissionInfo, BorderLayout.CENTER);
    content.add(statusMessage, BorderLayout.SOUTH);
  }

  public static void main(String[] args) {
    Thread.currentThread().setUncaughtExceptionHandler((t, e) -> printStackTrace(e));

    Injector injector = Guice.createInjector(new POAGraderUIModule());

    EventBus bus = injector.getInstance(EventBus.class);
    registerEventLogger(bus);

    PlanOfAttackGrader ui = injector.getInstance(PlanOfAttackGrader.class);
    ui.display();

    if (args.length >= 1) {
      String gradeBookFileName = args[0];
      File gradeBookFile = new File(gradeBookFileName);
      bus.post(new LoadGradeBook(gradeBookFile));
    }

    DownloadPOASubmissionsRequest downloadPOASubmissionsRequest;
    if (args.length >= 3) {
      String email = args[1];
      String password = loadPasswordFromFile(args[2]);
      downloadPOASubmissionsRequest = new DownloadPOASubmissionsRequest(email, password);

    } else {
      downloadPOASubmissionsRequest = new DownloadPOASubmissionsRequest();
    }

    bus.post(downloadPOASubmissionsRequest);
  }

  private static String loadPasswordFromFile(String fileName) {
    File file = new File(fileName);
    if (!file.exists()) {
      System.err.println("Password file does not exist: " + file);
      System.exit(1);
    }

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      return br.readLine();

    } catch (IOException e) {
      System.err.println("While reading password from " + file);
      e.printStackTrace(System.err);
      System.exit(1);
      return null;
    }
  }

  private static void printStackTrace(Throwable e) {
    e.fillInStackTrace();
    e.printStackTrace(System.err);
  }

  private void display() {
    Dimension fullScreen = Toolkit.getDefaultToolkit().getScreenSize();
    parent.setPreferredSize(fullScreen);
    int width = (int) (fullScreen.getWidth());
    int height = (int) (fullScreen.getHeight());
    parent.setPreferredSize(new Dimension(width, height));

    parent.pack();
    parent.setVisible(true);
  }

  private static void registerEventLogger(EventBus bus) {
    bus.register(new Object() {
      @Subscribe
      public void logEvent(Object event) {
        logger.debug("Event " + event);
      }
    });

  }

  private static POASubmission createPOASubmission(String subject) {
    POASubmission.Builder builder = POASubmission.builder();

    builder.setSubject(subject);
    builder.setSubmitter("Submitter");
    builder.setSubmitTime(LocalDateTime.now());

    return builder.create();
  }

}
