package edu.pdx.cs410J.grader.poa;

public interface EmailCredentialsView {
  public void setIsVisible(boolean isVisible);

  void addEmailAddressValueListener(EmailAddressValueListener listener);

  void addPasswordValueListener(PasswordValueListener listener);

  void addSubmitCredentialsListener(SubmitCredentialsListener listener);

  void setEmailAddress(String emailAddress);

  void setPassword(String password);

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
