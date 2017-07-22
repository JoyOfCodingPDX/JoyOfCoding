package edu.pdx.cs410J.grader.scoring.ui;

import ch.qos.logback.classic.Level;
import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.mvp.ui.UIMain;
import edu.pdx.cs410J.grader.poa.ui.TopLevelJFrame;
import edu.pdx.cs410J.grader.scoring.ProjectSubmission;
import edu.pdx.cs410J.grader.scoring.ProjectSubmissionsLoaded;
import edu.pdx.cs410J.grader.scoring.TestCaseOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ProjectSubmissionScorer extends UIMain {
  private static final Logger logger = LoggerFactory.getLogger(ProjectSubmissionScorer.class);

  @Inject
  public ProjectSubmissionScorer(TopLevelJFrame parent, ProjectSubmissionsPanel submissions, TestCasesPanel testCases) {
    super(parent);

    parent.setTitle("Project Submission Scorer");

    Container content = parent.getContentPane();
    content.setLayout(new BorderLayout());

    JPanel submissionsAndTestCases = new JPanel();
    submissionsAndTestCases.setLayout(new BorderLayout());
    submissionsAndTestCases.add(submissions, BorderLayout.WEST);
    submissionsAndTestCases.add(testCases, BorderLayout.EAST);

    content.add(submissionsAndTestCases, BorderLayout.WEST);
  }

  static void setLoggingLevelToDebug() {
    ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("edu.pdx.cs410J.grader");
    logger.setLevel(Level.DEBUG);
  }

  public static void main(String[] args) {
    setLoggingLevelToDebug();
    logAllUncaughtExceptions(logger);

    Injector injector = Guice.createInjector(new ProjectSubmissionScorerUIModule());

    EventBus bus = injector.getInstance(EventBus.class);
    logAllEventsOnBusAtDebugLevel(bus, logger);

    ProjectSubmissionScorer scorer = injector.getInstance(ProjectSubmissionScorer.class);
    scorer.display();

    bus.post(new ProjectSubmissionsLoaded(createProjectSubmissions()));
  }

  private static List<ProjectSubmission> createProjectSubmissions() {
    List<ProjectSubmission> submissions = new ArrayList<>();
    String projectName = "Project";
    for (int i = 0; i < 50; i++) {
      String studentId = "student" + i;
      ProjectSubmission submission = new ProjectSubmission();
      submission.setProjectName(projectName);
      submission.setStudentId(studentId);

      for (int j = 0; j < 10; j++) {
        String testCaseName = studentId + "'s test " + j;
        submission.addTestCaseOutput(new TestCaseOutput().setName(testCaseName));
      }

      submissions.add(submission);
    }

    return submissions;
  }

}
