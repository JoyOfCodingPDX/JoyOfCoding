package edu.pdx.cs410J.grader.scoring;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class GenerateProjectSubmissionXmlFiles {

  public static void main(String... args) {
    if (args.length == 0) {
      usage("Missing directory name");
      return;
    }

    String directoryName = args[0];
    File directory = new File(directoryName);
    if (directory.exists() && !directory.isDirectory()) {
      usage("Directory " + directory + " must be a directory");
      return;
    }

    directory.mkdirs();

    createProjectSubmissions().forEach(s -> writeXmlToDirectory(s, directory));
  }

  private static void writeXmlToDirectory(ProjectSubmission submission, File directory) {
    String xmlFileName = submission.getStudentId() + ".xml";
    File file = new File(directory, xmlFileName);
    ProjectSubmissionXmlConverter converter = null;
    try {
      converter = new ProjectSubmissionXmlConverter();
      converter.convertToXml(submission, new FileWriter(file));

    } catch (JAXBException | IOException e) {
      e.printStackTrace(System.err);
    }
  }

  private static List<ProjectSubmission> createProjectSubmissions() {
    List<ProjectSubmission> submissions = new ArrayList<>();
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

      submissions.add(submission);
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


  private static void usage(String message) {
    PrintStream err = System.err;

    err.println("** " + message);
    err.println();
    err.println("usage: java GenerateProjectSubmissionXmlFiles directoryName");
    err.println("  directoryName      The name of the directory into which XML files should be generated");
    err.println();

    System.exit(1);
  }
}
