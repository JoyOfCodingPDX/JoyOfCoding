package edu.pdx.cs410J.grader.scoring;

public class TestCaseOutput {
  private String name;
  private String description;
  private String command;
  private String output;

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
}
