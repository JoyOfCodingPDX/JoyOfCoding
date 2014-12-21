package edu.pdx.cs410J.grader.poa.ui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.poa.POASubmission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

@Singleton
public class PlanOfAttackGrader extends JFrame {
  private static final Logger logger = LoggerFactory.getLogger(PlanOfAttackGrader.class);

  @Inject
  public PlanOfAttackGrader(POASubmissionsPanel submissions, POASubmissionInformationPanel submissionInfo) {
    super("Plan Of Attack Grader");

    Container content = this.getContentPane();
    content.setLayout(new BorderLayout());
    content.add(submissions, BorderLayout.WEST);
    content.add(submissionInfo, BorderLayout.CENTER);
  }

  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new POAGraderUIModule());

    EventBus bus = injector.getInstance(EventBus.class);
    registerEventLogger(bus);

    PlanOfAttackGrader ui = injector.getInstance(PlanOfAttackGrader.class);
    ui.pack();
    ui.setVisible(true);

    publishDemoSubmissions(bus);
  }

  private static void registerEventLogger(EventBus bus) {
    bus.register(new Object() {
      @Subscribe
      public void logEvent(Object event) {
        logger.info("Event " + event);
      }
    });

  }

  private static void publishDemoSubmissions(EventBus bus) {
    bus.post(createPOASubmission("Submission 1"));
    bus.post(createPOASubmission("Submission 2"));
    bus.post(createPOASubmission("Submission 3"));
    bus.post(createPOASubmission("Submission 4"));
    bus.post(createPOASubmission("Submission 5"));
  }

  private static POASubmission createPOASubmission(String subject) {
    POASubmission.Builder builder = POASubmission.builder();

    builder.setSubject(subject);
    builder.setSubmitter("Submitter");
    builder.setSubmitTime(LocalDateTime.now());

    return builder.create();
  }

}
