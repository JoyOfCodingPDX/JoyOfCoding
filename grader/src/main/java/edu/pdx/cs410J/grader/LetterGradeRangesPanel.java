package edu.pdx.cs410J.grader;

import javax.swing.*;

public class LetterGradeRangesPanel extends JPanel {

  LetterGradeRangesPanel() {
    addLetterGradeRanges();
  }

  private void addLetterGradeRanges() {
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    for (LetterGrade letter : LetterGrade.values()) {
      this.add(new JLabel(letter.asString()));
    }

    this.add(Box.createVerticalGlue());

  }

}
