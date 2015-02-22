package edu.pdx.cs410J.grader.poa.ui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.poa.DownloadPOASubmissionsRequest;
import edu.pdx.cs410J.grader.poa.LoadGradeBook;
import edu.pdx.cs410J.grader.poa.POASubmission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;

@Singleton
public class PlanOfAttackGrader {
  private static final Logger logger = LoggerFactory.getLogger(PlanOfAttackGrader.class);
  private final TopLevelJFrame parent;

  @Inject
  public PlanOfAttackGrader(TopLevelJFrame parent, POASubmissionsPanel submissions, POASubmissionInformationPanel submissionInfo) {
    this.parent = parent;

    parent.setTitle("Plan Of Attack Grader");

    Container content = parent.getContentPane();
    content.setLayout(new BorderLayout());
    content.add(submissions, BorderLayout.WEST);
    content.add(submissionInfo, BorderLayout.CENTER);
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

    bus.post(new DownloadPOASubmissionsRequest());
  }

  private static void printStackTrace(Throwable e) {
    e.fillInStackTrace();
    e.printStackTrace(System.err);
  }

  private void display() {
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
