package edu.pdx.cs410J.grader.scoring.ui;

import ch.qos.logback.classic.Level;
import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.mvp.ui.TopLevelJFrame;
import edu.pdx.cs410J.grader.mvp.ui.UIMain;
import edu.pdx.cs410J.grader.scoring.ProjectSubmission;
import edu.pdx.cs410J.grader.scoring.ProjectSubmissionsLoaded;
import edu.pdx.cs410J.grader.scoring.ProjectSubmissionsLoaded.LoadedProjectSubmission;
import edu.pdx.cs410J.grader.scoring.TestCaseOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ProjectSubmissionsScorer extends UIMain {
  private static final Logger logger = LoggerFactory.getLogger(ProjectSubmissionsScorer.class);

  @Inject
  public ProjectSubmissionsScorer(TopLevelJFrame parent, ProjectSubmissionsPanel submissions, TestCasesPanel testCases,
                                  TestCaseOutputPanel testCaseOutput, ProjectSubmissionScorePanel score,
                                  ProjectSubmissionsLoaderSaverPanel loadAndSave) {
    super(parent);

    parent.setTitle("Project Submission Scorer");

    Container content = parent.getContentPane();
    content.setLayout(new BorderLayout());

    JPanel submissionsAndTestCases = new JPanel(new BorderLayout());
    submissionsAndTestCases.add(loadAndSave, BorderLayout.NORTH);
    submissionsAndTestCases.add(submissions, BorderLayout.WEST);
    submissionsAndTestCases.add(testCases, BorderLayout.EAST);

    content.add(submissionsAndTestCases, BorderLayout.WEST);

    JPanel scoreAndOutput = new JPanel(new BorderLayout());
    scoreAndOutput.add(score, BorderLayout.NORTH);
    scoreAndOutput.add(testCaseOutput, BorderLayout.CENTER);

    content.add(scoreAndOutput, BorderLayout.CENTER);
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

    ProjectSubmissionsScorer scorer = injector.getInstance(ProjectSubmissionsScorer.class);
    scorer.display();

    bus.post(new ProjectSubmissionsLoaded(createProjectSubmissions()));
  }

  private static List<LoadedProjectSubmission> createProjectSubmissions() {
    List<LoadedProjectSubmission> submissions = new ArrayList<>();
    String projectName = "Project";
    for (int i = 0; i < 50; i++) {
      String studentId = "student" + i;
      ProjectSubmission submission = new ProjectSubmission();
      submission.setProjectName(projectName);
      submission.setStudentId(studentId);
      submission.setTotalPoints(8.0);

      for (int j = 0; j < 10; j++) {
        String testCaseName = studentId + "'s test " + j;
        TestCaseOutput testCaseOutput =
          new TestCaseOutput()
            .setName(testCaseName)
            .setDescription("Test " + j + ": Test " + studentId + "'s " + projectName + " submission")
            .setCommand("java -jar " + studentId + "/project.jar Test" + j)
            .setOutput(generateOutput(studentId, j));
        submission.addTestCaseOutput(testCaseOutput);
      }

      File file = new File(new File(System.getProperty("user.dir")), studentId + ".xml");
      submissions.add(new LoadedProjectSubmission(file, submission));
    }

    return submissions;
  }

  private static String generateOutput(String studentId, int j) {
    String output = "Output of " + studentId + "'s Test " + j;
    if (j % 2 == 0) {
      for (int i = 0; i < 100; i++) {
        output += "\nq";
      }

    }

    return output;
  }

}
