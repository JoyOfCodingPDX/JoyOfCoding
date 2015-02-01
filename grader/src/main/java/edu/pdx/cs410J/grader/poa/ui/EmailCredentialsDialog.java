package edu.pdx.cs410J.grader.poa.ui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.poa.EmailCredentialsView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

@Singleton
public class EmailCredentialsDialog extends JDialog implements EmailCredentialsView {

  private final JTextField emailAddressField = new JTextField(30);
  private final JPasswordField passwordField = new JPasswordField(30);
  private final JButton okButton = new JButton("OK");
  private final JButton cancelButton = new JButton("Cancel");

  @Inject
  public EmailCredentialsDialog(TopLevelJFrame parent) {
    super(parent, "Enter Email Credentials", true);
    this.setVisible(false);

    Container rootPane1 = this.getContentPane();
    rootPane1.setLayout(new BorderLayout());
    rootPane1.add(createCredentialsPanel(), BorderLayout.CENTER);
    rootPane1.add(createOkCancelPanel(), BorderLayout.SOUTH);

    this.cancelButton.addActionListener(e -> hideDialog());
  }

  private JPanel createOkCancelPanel() {
    JPanel panel = new JPanel();
    BoxLayout layout = new BoxLayout(panel, BoxLayout.LINE_AXIS);
    panel.setLayout(layout);

    panel.add(Box.createHorizontalGlue());
    panel.add(okButton);
    panel.add(cancelButton);

    return panel;
  }

  private JPanel createCredentialsPanel() {
    JPanel credentialsPanel = new JPanel(new GridBagLayout());
    addComponentOnGrid(new JLabel("Email Address:"), credentialsPanel, 0, 0);
    addComponentOnGrid(this.emailAddressField, credentialsPanel, 0, 1);
    addComponentOnGrid(new JLabel("Password:"), credentialsPanel, 1, 0);
    addComponentOnGrid(this.passwordField, credentialsPanel, 1, 1);

    return credentialsPanel;
  }

  private void addComponentOnGrid(JComponent component, JPanel container, int row, int column) {
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.gridx = column;
    constraints.gridy = row;

    container.add(component, constraints);
  }


  @Override
  public void setIsVisible(boolean isVisible) {
    this.pack();
    this.setVisible(isVisible);
  }

  @Override
  public void addEmailAddressValueListener(EmailAddressValueListener listener) {
    this.emailAddressField.addFocusListener(new FocusListener() {
      @Override
      public void focusGained(FocusEvent e) {

      }

      @Override
      public void focusLost(FocusEvent e) {
        listener.setEmailAddress(emailAddressField.getText());
      }
    });
  }

  @Override
  public void addPasswordValueListener(PasswordValueListener listener) {
    this.passwordField.addFocusListener(new FocusListener() {
      @Override
      public void focusGained(FocusEvent e) {

      }

      @Override
      public void focusLost(FocusEvent e) {
        listener.setPassword(new String(passwordField.getPassword()));
      }
    });
  }

  @Override
  public void addSubmitCredentialsListener(SubmitCredentialsListener listener) {
    this.okButton.addActionListener(e -> submitCredentials(listener));
  }

  private void submitCredentials(SubmitCredentialsListener listener) {
    listener.submitCredentials();
    hideDialog();
  }

  private void hideDialog() {
    this.setVisible(false);
  }

}
