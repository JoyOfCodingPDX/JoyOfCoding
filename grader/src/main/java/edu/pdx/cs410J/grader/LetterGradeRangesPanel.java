package edu.pdx.cs410J.grader;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class LetterGradeRangesPanel extends JPanel {

  private final Map<LetterGrade, MinMaxValueFields> minMaxValueFields = new HashMap<>();

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

    MinMaxValueFields fields = createMinMaxValueFieldsFor(letter);

    rangePanel.add(fields.getMinValueField());
    rangePanel.add(new JLabel("to"));
    rangePanel.add(fields.getMaxValueField());

    rangePanel.add(Box.createHorizontalBox());

    this.add(rangePanel);
  }

  private MinMaxValueFields createMinMaxValueFieldsFor(LetterGrade letter) {
    MinMaxValueFields fields = new MinMaxValueFields(new JTextField(2), new JTextField(2));
    this.minMaxValueFields.put(letter, fields);
    return fields;
  }

  public void displayLetterGradeRanges(GradeBook.LetterGradeRanges letterGradeRanges) {
    letterGradeRanges.forEach(this::displayLetterGradeRange);
  }

  private void displayLetterGradeRange(GradeBook.LetterGradeRanges.LetterGradeRange range) {
    setTextFieldValue(getMinValueField(range.letterGrade()), range.minimum());
    setTextFieldValue(getMaxValueField(range.letterGrade()), range.maximum());
  }

  private void setTextFieldValue(JTextField field, int value) {
    field.setText(String.valueOf(value));
  }

  private JTextField getMinValueField(LetterGrade letterGrade) {
    MinMaxValueFields fields = this.minMaxValueFields.get(letterGrade);
    return fields.getMinValueField();
  }

  private JTextField getMaxValueField(LetterGrade letterGrade) {
    MinMaxValueFields fields = this.minMaxValueFields.get(letterGrade);
    return fields.getMaxValueField();
  }

  private class MinMaxValueFields {
    private final JTextField minValueField;
    private final JTextField maxValueField;

    private MinMaxValueFields(JTextField minValueField, JTextField maxValueField) {
      this.minValueField = minValueField;
      this.maxValueField = maxValueField;
    }

    public JTextField getMinValueField() {
      return minValueField;
    }

    public JTextField getMaxValueField() {
      return maxValueField;
    }
  }
}
