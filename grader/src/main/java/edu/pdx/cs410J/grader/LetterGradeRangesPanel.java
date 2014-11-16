package edu.pdx.cs410J.grader;

import javax.swing.*;

public class LetterGradeRangesPanel extends JPanel {

  LetterGradeRangesPanel() {
    addLetterGradeRanges();
  }

  private void addLetterGradeRanges() {
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    for (LetterGrade letter : LetterGrade.values()) {
      if (letter.hasNumericRange()) {
        addLetterGradeRange(letter);
      }
    }

    this.add(Box.createVerticalGlue());

    this.add(new JButton("Update"));

  }

  private void addLetterGradeRange(LetterGrade letter) {
    JPanel rangePanel = new JPanel();
    rangePanel.setLayout(new BoxLayout(rangePanel, BoxLayout.X_AXIS));

    rangePanel.add(new JLabel(letter.asString()));
    rangePanel.add(new JTextField(2));
    rangePanel.add(new JLabel("to"));
    rangePanel.add(new JTextField(2));

    rangePanel.add(Box.createHorizontalBox());

    this.add(rangePanel);
  }

}
