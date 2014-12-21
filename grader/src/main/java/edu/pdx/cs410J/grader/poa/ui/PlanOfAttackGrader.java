package edu.pdx.cs410J.grader.poa.ui;

import com.google.common.eventbus.EventBus;
import edu.pdx.cs410J.grader.poa.POASubmission;
import edu.pdx.cs410J.grader.poa.POASubmissionsPresenter;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class PlanOfAttackGrader extends JFrame {

  public PlanOfAttackGrader(POASubmissionsPanel submissions) {
    super("Plan Of Attack Grader");

    Container content = this.getContentPane();
    content.setLayout(new BorderLayout());
    content.add(submissions, BorderLayout.CENTER);
  }

  public static void main(String[] args) {
    EventBus bus = new EventBus("PlanOfAttackGrader");

    createUI(bus);

    publishDemoSubmissions(bus);
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

  private static void createUI(EventBus bus) {
    POASubmissionsPanel submissions = new POASubmissionsPanel();
    PlanOfAttackGrader ui = new PlanOfAttackGrader(submissions);
    ui.pack();
    ui.setVisible(true);

    new POASubmissionsPresenter(bus, submissions);

  }
}
