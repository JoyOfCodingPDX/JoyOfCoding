package edu.pdx.cs410J.grader.poa;

public class EmailCredentials {
  private final String emailAddress;
  private final String password;

  public EmailCredentials(String emailAddress, String password) {
    this.emailAddress = emailAddress;
    this.password = password;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public String getPassword() {
    return password;
  }

  @Override
  public String toString() {
    return "Email credentials for " + this.emailAddress;
  }
}
