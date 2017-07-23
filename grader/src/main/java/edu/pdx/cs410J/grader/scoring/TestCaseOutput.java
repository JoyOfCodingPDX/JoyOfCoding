package edu.pdx.cs410J.grader.scoring;

public class TestCaseOutput {
  private String name;
  private String description;
  private String command;
  private String output;
  private Double pointsDeducted;
  private String graderComment;

  public TestCaseOutput setName(String name) {
    this.name = name;
    return this;
  }

  public String getName() {
    return name;
  }

  public TestCaseOutput setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public TestCaseOutput setCommand(String command) {
    this.command = command;
    return this;
  }

  public String getCommand() {
    return command;
  }

  public TestCaseOutput setOutput(String output) {
    this.output = output;
    return this;
  }

  public String getOutput() {
    return output;
  }

  public TestCaseOutput setPointsDeducted(Double pointsDeducted) {
    this.pointsDeducted = pointsDeducted;
    return this;
  }

  public Double getPointsDeducted() {
    return pointsDeducted;
  }

  public TestCaseOutput setGraderComment(String graderComment) {
    this.graderComment = graderComment;
    return this;
  }

  public String getGraderComment() {
    return graderComment;
  }
}
