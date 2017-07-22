package edu.pdx.cs410J.grader.scoring;

import java.util.List;

public class ProjectSubmissionsLoaded {
  private final List<ProjectSubmission> submissions;

  public ProjectSubmissionsLoaded(List<ProjectSubmission> submissions) {
    this.submissions = submissions;
  }

  public List<ProjectSubmission> getSubmissions() {
    return this.submissions;
  }
}
