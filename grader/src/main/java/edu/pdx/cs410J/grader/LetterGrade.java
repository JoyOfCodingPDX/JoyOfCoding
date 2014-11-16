package edu.pdx.cs410J.grader;

public enum LetterGrade {

  A("A"),
  A_MINUS("A-"),
  B_PLUS("B+"),
  B("B"),
  B_MINUS("B-"),
  C_PLUS("C+"),
  C("C"),
  C_MINUS("C-"),
  D_PLUS("D+"),
  D("D"),
  D_MINUS("D-"),
  F("F"),
  I("I", false),
  X("X", false)
  ;

  private final String stringValue;
  private final boolean hasNumericRange;

  LetterGrade(String stringValue) {
    this(stringValue, true);
  }

  LetterGrade(String stringValue, boolean hasNumericRange) {
    this.stringValue = stringValue;
    this.hasNumericRange = hasNumericRange;
  }

  public static LetterGrade fromString(String string) {
    for (LetterGrade grade : values()) {
      if (grade.asString().equals(string)) {
        return grade;
      }
    }

    throw new IllegalArgumentException("Could not find LetterGrade for string \"" + string + "\"");
  }

  public String asString() {
    return stringValue;
  }

  public String toString() {
    return asString();
  }

  public boolean hasNumericRange() {
    return this.hasNumericRange;
  }
}
