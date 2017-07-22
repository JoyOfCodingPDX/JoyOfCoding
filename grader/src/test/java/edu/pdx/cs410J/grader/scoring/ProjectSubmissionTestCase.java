package edu.pdx.cs410J.grader.scoring;

import edu.pdx.cs410J.grader.mvp.EventBusTestCase;

public class ProjectSubmissionTestCase extends EventBusTestCase {
  protected ProjectSubmission createProjectSubmission(String projectName, String studentId) {
    ProjectSubmission submission = new ProjectSubmission();
    submission.setProjectName(projectName);
    submission.setStudentId(studentId);
    return submission;
  }
}
