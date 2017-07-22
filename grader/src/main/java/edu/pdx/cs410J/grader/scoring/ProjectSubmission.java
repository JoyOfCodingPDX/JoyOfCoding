package edu.pdx.cs410J.grader.scoring;

public class ProjectSubmission {
  private String projectName;
  private String studentId;

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setStudentId(String studentId) {
    this.studentId = studentId;
  }

  public String getStudentId() {
    return studentId;
  }
}
