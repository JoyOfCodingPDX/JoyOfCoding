package edu.pdx.cs410J.grader.scoring;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "project-submission")
public class ProjectSubmission {
  private String projectName;
  private String studentId;
  private final List<TestCaseOutput> testCaseOutputs = new ArrayList<>();
  private double totalPoints;
  private Double score;
  private String studentName;
  private Date submissionTime;

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

  public String getStudentName() {
    return studentName;
  }

  public void setStudentName(String studentName) {
    this.studentName = studentName;
  }

  public Date getSubmissionTime() {
    return submissionTime;
  }

  public void setSubmissionTime(Date submissionTime) {
    this.submissionTime = submissionTime;
  }
}
