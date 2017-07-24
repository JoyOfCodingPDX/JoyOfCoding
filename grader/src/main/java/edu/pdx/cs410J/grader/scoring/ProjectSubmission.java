package edu.pdx.cs410J.grader.scoring;

import java.util.ArrayList;
import java.util.List;

public class ProjectSubmission {
  private String projectName;
  private String studentId;
  private final List<TestCaseOutput> testCaseOutputs = new ArrayList<>();
  private double totalPoints;
  private Double score;

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

  public void addTestCaseOutput(TestCaseOutput testCaseOutput) {
    this.testCaseOutputs.add(testCaseOutput);
  }

  public List<TestCaseOutput> getTestCaseOutputs() {
    return testCaseOutputs;
  }

  public void setTotalPoints(double totalPoints) {
    this.totalPoints = totalPoints;
  }

  public double getTotalPoints() {
    return totalPoints;
  }

  public ProjectSubmission setScore(Double score) {
    this.score = score;
    return this;
  }

  public Double getScore() {
    return score;
  }
}
