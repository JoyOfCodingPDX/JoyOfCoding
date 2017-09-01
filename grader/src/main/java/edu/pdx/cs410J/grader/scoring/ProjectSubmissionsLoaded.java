package edu.pdx.cs410J.grader.scoring;

import java.io.File;
import java.util.List;

public class ProjectSubmissionsLoaded {
  private final List<LoadedProjectSubmission> submissions;

  public ProjectSubmissionsLoaded(List<LoadedProjectSubmission> submissions) {
    this.submissions = submissions;
  }

  public List<LoadedProjectSubmission> getLoadedSubmissions() {
    return this.submissions;
  }

  public static class LoadedProjectSubmission {
    private final File file;
    private final ProjectSubmission submission;

    public LoadedProjectSubmission(File file, ProjectSubmission submission) {
      this.file = file;
      this.submission = submission;
    }

    public ProjectSubmission getSubmission() {
      return submission;
    }

    public File getFile() {
      return file;
    }
  }
}
