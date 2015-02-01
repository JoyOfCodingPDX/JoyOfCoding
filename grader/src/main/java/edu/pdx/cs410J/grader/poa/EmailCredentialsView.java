package edu.pdx.cs410J.grader.poa;

public interface EmailCredentialsView {
  public void setVisible(boolean isVisible);

  void addEmailAddressValueListener(EmailAddressValueListener listener);

  void addPasswordValueListener(PasswordValueListener listener);

  void addSubmitCredentialsListener(SubmitCredentialsListener listener);

  public interface EmailAddressValueListener {
    void setEmailAddress(String address);
  }

  public interface PasswordValueListener {
    void setPassword(String password);
  }

  public interface SubmitCredentialsListener {
    void submitCredentials();
  }
}
