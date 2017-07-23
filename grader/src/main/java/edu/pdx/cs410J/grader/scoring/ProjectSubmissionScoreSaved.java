package edu.pdx.cs410J.grader.scoring;

public class ProjectSubmissionScoreSaved {
  private ProjectSubmission projectSubmission;

  public ProjectSubmissionScoreSaved(ProjectSubmission submission) {
    this.projectSubmission = submission;
  }

  public ProjectSubmission getProjectSubmission() {
    return projectSubmission;
  }
}
