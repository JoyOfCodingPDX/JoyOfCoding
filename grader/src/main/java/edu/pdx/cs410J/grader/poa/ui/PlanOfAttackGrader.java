package edu.pdx.cs410J.grader.poa.ui;

import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.poa.POASubmission;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

@Singleton
public class PlanOfAttackGrader extends JFrame {

  @Inject
  public PlanOfAttackGrader(POASubmissionsPanel submissions) {
    super("Plan Of Attack Grader");

    Container content = this.getContentPane();
    content.setLayout(new BorderLayout());
    content.add(submissions, BorderLayout.CENTER);
  }

  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new POAGraderUIModule());

    PlanOfAttackGrader ui = injector.getInstance(PlanOfAttackGrader.class);
    ui.pack();
    ui.setVisible(true);

    publishDemoSubmissions(injector.getInstance(EventBus.class));
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
