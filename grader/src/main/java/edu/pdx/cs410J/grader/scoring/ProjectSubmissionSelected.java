package edu.pdx.cs410J.grader.scoring;

public class ProjectSubmissionSelected {
  private final ProjectSubmission projectSubmission;

  public ProjectSubmissionSelected(ProjectSubmission submission) {
    this.projectSubmission = submission;
  }

  public ProjectSubmission getProjectSubmission() {
    return projectSubmission;
  }

  @Override
  public String toString() {
    return "Selected project submission " + projectSubmission.getStudentId() + "/" + projectSubmission.getProjectName();
  }
}
