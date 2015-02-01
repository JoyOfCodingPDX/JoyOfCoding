package edu.pdx.cs410J.grader.poa;

public interface EmailCredentialsView {
  public void setVisible(boolean isVisible);

  void addEmailAddressValueListener(EmailAddressValueListener listener);

  public interface EmailAddressValueListener {
    void setEmailAddress(String address);
  }
}
