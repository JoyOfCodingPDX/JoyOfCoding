package edu.pdx.cs410J.grader;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static edu.pdx.cs410J.grader.GradeBook.LetterGradeRanges.LetterGradeRange;

public class LetterGradeRangesPanel extends JPanel {

  private final Map<LetterGrade, MinMaxValueFields> minMaxValueFields = new HashMap<>();
  private GradeBook.LetterGradeRanges letterGradeRanges;

  LetterGradeRangesPanel() {
    addLetterGradeRanges();
  }

  private void addLetterGradeRanges() {
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    this.add(createRangesPanel());

    this.add(Box.createVerticalGlue());

    JButton update = new JButton("Update");
    update.addActionListener(e -> updateLetterGradeRanges());
    this.add(update);

  }

  private JPanel createRangesPanel() {
    JPanel rangesPanel = new JPanel(new GridBagLayout());
    int row = 0;

    for (LetterGrade letter : LetterGrade.values()) {
      if (letter.hasNumericRange()) {

        addLetterLabel(letter, rangesPanel, row);

        MinMaxValueFields fields = createMinMaxValueFieldsFor(letter);

        addMaxValueField(fields, rangesPanel, row);
        addToLabel(rangesPanel, row);
        addMinValueField(fields, rangesPanel, row);

        row++;
      }
    }

    return rangesPanel;
  }

  private void addMinValueField(MinMaxValueFields fields, JPanel rangesPanel, int row) {
    addComponentToRow(fields.getMinValueField(), rangesPanel, row, 3);
  }

  private void addToLabel(JPanel rangesPanel, int row) {
    addComponentToRow(new JLabel("to"), rangesPanel, row, 2);
  }

  private void addMaxValueField(MinMaxValueFields fields, JPanel rangesPanel, int row) {
    addComponentToRow(fields.getMaxValueField(), rangesPanel, row, 1);
  }

  private void addLetterLabel(LetterGrade letter, JPanel rangesPanel, int row) {
    JLabel letterLabel = new JLabel(letter.asString());
    addComponentToRow(letterLabel, rangesPanel, row, 0);
  }

  private void addComponentToRow(JComponent component, JPanel rangesPanel, int row, int gridx) {
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.gridx = gridx;
    constraints.gridy = row;
    rangesPanel.add(component, constraints);
  }

  private void updateLetterGradeRanges() {
    this.minMaxValueFields.forEach((LetterGrade grade, MinMaxValueFields fields) -> {
      int minValue = getIntValue(fields.getMinValueField());
      int maxValue = getIntValue(fields.getMaxValueField());

      LetterGradeRange range = this.letterGradeRanges.getRange(grade);
      range.setRange(minValue, maxValue);
    });

    this.letterGradeRanges.validate();
  }

  private int getIntValue(JTextField field) {
    return Integer.parseInt(field.getText());
  }

  private MinMaxValueFields createMinMaxValueFieldsFor(LetterGrade letter) {
    MinMaxValueFields fields = new MinMaxValueFields(new JTextField(3), new JTextField(3));
    this.minMaxValueFields.put(letter, fields);
    return fields;
  }

  public void displayLetterGradeRanges(GradeBook.LetterGradeRanges letterGradeRanges) {
    clearAllMinMaxValueFields();

    this.letterGradeRanges = letterGradeRanges;
    letterGradeRanges.forEach(this::displayLetterGradeRange);
  }

  private void clearAllMinMaxValueFields() {
    this.minMaxValueFields.values().forEach(MinMaxValueFields::clearFields);
  }

  private void displayLetterGradeRange(LetterGradeRange range) {
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

    public void clearFields() {
      this.maxValueField.setText("");
      this.minValueField.setText("");
    }
  }
}
