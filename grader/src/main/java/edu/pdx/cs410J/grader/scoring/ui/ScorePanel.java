package edu.pdx.cs410J.grader.scoring.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.function.Consumer;

public abstract class ScorePanel extends JPanel {
  protected void registerListenerOnTextFieldChange(final JTextField field, final Consumer<String> consume) {
    field.addActionListener(e -> consume.accept(field.getText()));
    field.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        consume.accept(field.getText());
      }
    });
  }

  protected void setBorderBasedOnValidity(JTextField field, boolean isValid) {
    if (isValid) {
      field.setBorder(BorderFactory.createEmptyBorder());

    } else {
      field.setBorder(BorderFactory.createLineBorder(Color.red, 2));
    }
  }
}
