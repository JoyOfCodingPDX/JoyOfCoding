package edu.pdx.cs.joy.grader.poa;

public class DownloadPOASubmissionsRequest {

  private final String emailAddress;
  private final String password;

  public DownloadPOASubmissionsRequest() {
    this(null, null);
  }

  public DownloadPOASubmissionsRequest(String emailAddress, String password) {
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
    return "Download submissions";
  }

}
